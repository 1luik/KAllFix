package n1luik.KAllFix.util;

import net.minecraft.server.level.ServerPlayer;

public record ServerPlayerKey(ServerPlayer player) {
    @Override
    public int hashCode() {
        return player.getUUID().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerPlayerKey that = (ServerPlayerKey) o;
        return player == that.player;
    }
}
