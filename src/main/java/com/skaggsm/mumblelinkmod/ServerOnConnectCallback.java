package com.skaggsm.mumblelinkmod;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */

public interface ServerOnConnectCallback {
    Event<ServerOnConnectCallback> EVENT = EventFactory.createArrayBacked(ServerOnConnectCallback.class, (listeners) ->
            EventFactory.isProfilingEnabled() ?
                    (player) -> {
                        player.server.getProfiler().push("fabricServerOnConnect");

                        for (ServerOnConnectCallback event : listeners) {
                            player.server.getProfiler().push(EventFactory.getHandlerName(event));
                            event.onConnect(player);
                            player.server.getProfiler().pop();
                        }

                        player.server.getProfiler().pop();
                    } :
                    (player) -> {
                        for (ServerOnConnectCallback event : listeners) {
                            event.onConnect(player);
                        }

                    });

    void onConnect(ServerPlayerEntity player);
}
