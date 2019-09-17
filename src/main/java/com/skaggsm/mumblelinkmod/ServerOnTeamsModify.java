package com.skaggsm.mumblelinkmod;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */

public interface ServerOnTeamsModify {
    Event<ServerOnTeamsModify> EVENT = EventFactory.createArrayBacked(ServerOnTeamsModify.class, (listeners) ->
            EventFactory.isProfilingEnabled() ?
                    (scoreboard, server) -> {
                        server.getProfiler().push("fabricServerOnScoreboardUpdate");

                        for (ServerOnTeamsModify event : listeners) {
                            server.getProfiler().push(EventFactory.getHandlerName(event));
                            event.onScoreboardModify(scoreboard, server);
                            server.getProfiler().pop();
                        }

                        server.getProfiler().pop();
                    } :
                    (scoreboard, server) -> {
                        for (ServerOnTeamsModify event : listeners) {
                            event.onScoreboardModify(scoreboard, server);
                        }

                    });

    void onScoreboardModify(ServerScoreboard scoreboard, MinecraftServer server);
}
