package net.combatroll.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.combatroll.client.animation.AnimatablePlayer;

import java.util.Objects;
import java.util.Random;

public final class RollEffect {
    public static final class Visuals {
        private final String animationName;
        private final Particles particles;

        public Visuals(String animationName, Particles particles) {
            this.animationName = animationName;
            this.particles = particles;
        }

        public String animationName() {
            return animationName;
        }

        public Particles particles() {
            return particles;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Visuals that = (Visuals) obj;
            return Objects.equals(this.animationName, that.animationName) &&
                    Objects.equals(this.particles, that.particles);
        }

        @Override
        public int hashCode() {
            return Objects.hash(animationName, particles);
        }

        @Override
        public String toString() {
            return "Visuals[" +
                    "animationName=" + animationName + ", " +
                    "particles=" + particles + ']';
        }
    }

    public enum Particles {
        PUFF
    }

    private static Random random = new Random();
    private final Visuals visuals;
    private final String soundId;

    public RollEffect(Visuals visuals, String soundId) {
        this.visuals = visuals;
        this.soundId = soundId;
    }

    public static void playVisuals(Visuals visuals, PlayerEntity player, Vec3d direction) {
        ((AnimatablePlayer) player).playRollAnimation(visuals.animationName(), direction);
        if (CombatRollClient.config.playRollSound) {
            SoundEvent sound = Registry.SOUND_EVENT.get(new Identifier("combatroll:roll"));
            if (sound != null) {
                player.world.playSound(player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 1, 1, true);
            }
        }
        if (visuals.particles() == Particles.PUFF) {
            for (int i = 0; i < 15; ++i) {
                double d = random.nextGaussian() * 0.02;
                double e = random.nextGaussian() * 0.02;
                double f = random.nextGaussian() * 0.02;
                player.world.addParticle(ParticleTypes.POOF,
                        player.getParticleX(1.5),
                        player.getBodyY(random.nextGaussian() * 0.3),
                        player.getParticleZ(1.5), d, e, f);
            }
        }
    }

    public Visuals visuals() {
        return visuals;
    }

    public String soundId() {
        return soundId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        RollEffect that = (RollEffect) obj;
        return Objects.equals(this.visuals, that.visuals) &&
                Objects.equals(this.soundId, that.soundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visuals, soundId);
    }

    @Override
    public String toString() {
        return "RollEffect[" +
                "visuals=" + visuals + ", " +
                "soundId=" + soundId + ']';
    }

}
