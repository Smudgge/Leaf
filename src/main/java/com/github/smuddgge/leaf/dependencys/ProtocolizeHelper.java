package com.github.smuddgge.leaf.dependencys;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;

import java.util.UUID;

/**
 * Represents the protocolize helper.
 */
public class ProtocolizeHelper {

    public void check() throws Exception {
        ProtocolizePlayer player = Protocolize.playerProvider().player(UUID.randomUUID());
    }
}
