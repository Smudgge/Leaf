package com.github.smuddgge.leaf.helper;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.dependency.ProtocolizeDependency;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SoundHelper {

    /**
     * Used to play a sound for a player.
     *
     * @param sound      The sound to play.
     * @param playerUuid The players uuid.
     */
    public static void play(@Nullable String sound, @Nullable UUID playerUuid) {
        if (sound == null) return;
        if (sound.equals("none")) return;

        // Check if protocolize is enabled.
        if (!ProtocolizeDependency.isEnabled()) {
            Leaf.get().getLogger().warn("Tried to use sounds when the dependency is not enabled.");
            Leaf.get().getLogger().warn("&7" + ProtocolizeDependency.getDependencyMessage());
            return;
        }

        ProtocolizePlayer player = Protocolize.playerProvider().player(playerUuid);
        player.playSound(Sound.valueOf(sound.toUpperCase()), SoundCategory.MASTER, 1f, 1f);
    }
}
