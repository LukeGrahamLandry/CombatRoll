package net.combatroll.client.animation;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class AdjustmentModifier extends AbstractModifier {
    public static final class PartModifier {
        private final Vec3f rotation;
        private final Vec3f offset;

        public PartModifier(
                Vec3f rotation,
                Vec3f offset
        ) {
            this.rotation = rotation;
            this.offset = offset;
        }

        public Vec3f rotation() {
            return rotation;
        }

        public Vec3f offset() {
            return offset;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            PartModifier that = (PartModifier) obj;
            return Objects.equals(this.rotation, that.rotation) &&
                    Objects.equals(this.offset, that.offset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rotation, offset);
        }

        @Override
        public String toString() {
            return "PartModifier[" +
                    "rotation=" + rotation + ", " +
                    "offset=" + offset + ']';
        }
    }

    ;

    public boolean enabled = true;

    private Function<String, Optional<PartModifier>> source;

    public AdjustmentModifier(Function<String, Optional<PartModifier>> source) {
        this.source = source;
    }

    private float getFadeIn(float delta) {
        float fadeIn = 1;
        if(this.getAnim() instanceof KeyframeAnimationPlayer) {
            KeyframeAnimationPlayer player = (KeyframeAnimationPlayer) this.getAnim();
            float currentTick = player.getTick() + delta;
            fadeIn = currentTick / (float) player.getData().beginTick;
            fadeIn = Math.min(fadeIn, 1F);
        }
        return fadeIn;
    }

    private float getFadeOut(float delta) {
        float fadeOut = 1;
        if(this.getAnim() instanceof KeyframeAnimationPlayer) {
            KeyframeAnimationPlayer player = (KeyframeAnimationPlayer) this.getAnim();
            float currentTick = player.getTick() + delta;

            float position = (-1F) * (currentTick - player.getData().stopTick);
            float length = player.getData().stopTick - player.getData().endTick;
            if (length > 0) {
                fadeOut = position / length;
                fadeOut = Math.min(fadeOut, 1F);
            }
        }
        return fadeOut;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        if (!enabled) {
            return super.get3DTransform(modelName, type, tickDelta, value0);
        }

        Optional<PartModifier> partModifier = source.apply(modelName);

        Vec3f modifiedVector = value0;
        float fade = getFadeIn(tickDelta) * getFadeOut(tickDelta);
        if (partModifier.isPresent()) {
            modifiedVector = super.get3DTransform(modelName, type, tickDelta, modifiedVector);
            return transformVector(modifiedVector, type, partModifier.get(), fade);
        } else {
            return super.get3DTransform(modelName, type, tickDelta, value0);
        }
    }

    private Vec3f transformVector(Vec3f vector, TransformType type, PartModifier partModifier, float fade) {
        if (type == TransformType.POSITION) {
            return vector.add(partModifier.offset);
        } else if (type == TransformType.ROTATION) {
            return vector.add(partModifier.rotation.scale(fade));
        } else if (type == TransformType.BEND) {// Nothing to do here...
        }
        return vector;
    }
}