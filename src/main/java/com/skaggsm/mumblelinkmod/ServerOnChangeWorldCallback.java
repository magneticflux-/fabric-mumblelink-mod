package com.skaggsm.mumblelinkmod;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */

public interface ServerOnChangeWorldCallback {
    Event<ServerOnChangeWorldCallback> EVENT = EventFactory.createArrayBacked(ServerOnChangeWorldCallback.class, (listeners) ->
            EventFactory.isProfilingEnabled() ?
                    (toDimension, player) -> {
                        player.server.getProfiler().push("fabricServerOnChangeDimension");

                        for (ServerOnChangeWorldCallback event : listeners) {
                            player.server.getProfiler().push(EventFactory.getHandlerName(event));
                            event.onChangeDimension(toDimension, player);
                            player.server.getProfiler().pop();
                        }

                        player.server.getProfiler().pop();
                    } :
                    (toDimension, player) -> {
                        for (ServerOnChangeWorldCallback event : listeners) {
                            event.onChangeDimension(toDimension, player);
                        }

                    });

    void onChangeDimension(RegistryKey<World> toDimension, ServerPlayerEntity player);
}
