package com.skaggsm.mumblelinkmod;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */

public interface ServerOnChangeDimensionCallback {
    Event<ServerOnChangeDimensionCallback> EVENT = EventFactory.createArrayBacked(ServerOnChangeDimensionCallback.class, (listeners) ->
            EventFactory.isProfilingEnabled() ?
                    (toDimension, player) -> {
                        player.server.getProfiler().push("fabricServerOnChangeDimension");

                        for (ServerOnChangeDimensionCallback event : listeners) {
                            player.server.getProfiler().push(EventFactory.getHandlerName(event));
                            event.onChangeDimension(toDimension, player);
                            player.server.getProfiler().pop();
                        }

                        player.server.getProfiler().pop();
                    } :
                    (toDimension, player) -> {
                        for (ServerOnChangeDimensionCallback event : listeners) {
                            event.onChangeDimension(toDimension, player);
                        }

                    });

    void onChangeDimension(DimensionType toDimension, ServerPlayerEntity player);
}
