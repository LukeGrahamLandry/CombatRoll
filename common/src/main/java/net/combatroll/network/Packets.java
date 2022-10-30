package net.combatroll.network;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.combatroll.CombatRoll;
import net.combatroll.client.RollEffect;
import net.combatroll.config.ServerConfig;

import java.util.Objects;

public class Packets {
    public static final class RollPublish {
            public static Identifier ID = new Identifier(CombatRoll.MOD_ID, "publish");
        private final int playerId;
        private final RollEffect.Visuals visuals;
        private final Vec3d velocity;

        public RollPublish(int playerId, RollEffect.Visuals visuals, Vec3d velocity) {
            this.playerId = playerId;
            this.visuals = visuals;
            this.velocity = velocity;
        }

            public PacketByteBuf write() {
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeInt(playerId);
                buffer.writeString(visuals.animationName());
                buffer.writeString(visuals.particles().toString());
                buffer.writeDouble(velocity.x);
                buffer.writeDouble(velocity.y);
                buffer.writeDouble(velocity.z);
                return buffer;
            }

            public static RollPublish read(PacketByteBuf buffer) {
                int playerId = buffer.readInt();
                RollEffect.Visuals visuals = new RollEffect.Visuals(
                        buffer.readString(),
                        RollEffect.Particles.valueOf(buffer.readString()));
                Vec3d velocity = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
                return new RollPublish(playerId, visuals, velocity);
            }

        public int playerId() {
            return playerId;
        }

        public RollEffect.Visuals visuals() {
            return visuals;
        }

        public Vec3d velocity() {
            return velocity;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            RollPublish that = (RollPublish) obj;
            return this.playerId == that.playerId &&
                    Objects.equals(this.visuals, that.visuals) &&
                    Objects.equals(this.velocity, that.velocity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerId, visuals, velocity);
        }

        @Override
        public String toString() {
            return "RollPublish[" +
                    "playerId=" + playerId + ", " +
                    "visuals=" + visuals + ", " +
                    "velocity=" + velocity + ']';
        }

        }

    public static final class RollAnimation {
            public static Identifier ID = new Identifier(CombatRoll.MOD_ID, "animation");
        private final int playerId;
        private final RollEffect.Visuals visuals;
        private final Vec3d velocity;

        public RollAnimation(int playerId, RollEffect.Visuals visuals, Vec3d velocity) {
            this.playerId = playerId;
            this.visuals = visuals;
            this.velocity = velocity;
        }

            public PacketByteBuf write() {
                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeInt(playerId);
                buffer.writeString(visuals.animationName());
                buffer.writeString(visuals.particles().toString());
                buffer.writeDouble(velocity.x);
                buffer.writeDouble(velocity.y);
                buffer.writeDouble(velocity.z);
                return buffer;
            }

            public static RollAnimation read(PacketByteBuf buffer) {
                int playerId = buffer.readInt();
                RollEffect.Visuals visuals = new RollEffect.Visuals(
                        buffer.readString(),
                        RollEffect.Particles.valueOf(buffer.readString()));
                Vec3d velocity = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
                return new RollAnimation(playerId, visuals, velocity);
            }

        public int playerId() {
            return playerId;
        }

        public RollEffect.Visuals visuals() {
            return visuals;
        }

        public Vec3d velocity() {
            return velocity;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            RollAnimation that = (RollAnimation) obj;
            return this.playerId == that.playerId &&
                    Objects.equals(this.visuals, that.visuals) &&
                    Objects.equals(this.velocity, that.velocity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerId, visuals, velocity);
        }

        @Override
        public String toString() {
            return "RollAnimation[" +
                    "playerId=" + playerId + ", " +
                    "visuals=" + visuals + ", " +
                    "velocity=" + velocity + ']';
        }

        }

    public static class ConfigSync {
        public static Identifier ID = new Identifier(CombatRoll.MOD_ID, "config_sync");

        public static PacketByteBuf write(ServerConfig config) {
            Gson gson = new Gson();
            String json = gson.toJson(config);
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeString(json);
            return buffer;
        }

        public static ServerConfig read(PacketByteBuf buffer) {
            Gson gson = new Gson();
            String json = buffer.readString();
            ServerConfig config = gson.fromJson(json, ServerConfig.class);
            return config;
        }
    }
}
