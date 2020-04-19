package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.ServerOnTeamsModify;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */
@Mixin(ServerScoreboard.class)
public class MixinServerScoreboard {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "addPlayerToTeam", at = @At(value = "RETURN", ordinal = 0))
    private void onAddPlayerToTeam(String playerName, Team team, CallbackInfoReturnable<Boolean> cir) {
        doOnScoreboardModify();
    }

    @Inject(method = "removePlayerFromTeam", at = @At("TAIL"))
    private void onRemovePlayerFromTeam(String playerName, Team team, CallbackInfo ci) {
        doOnScoreboardModify();
    }

    @Inject(method = "updateRemovedTeam", at = @At("TAIL"))
    private void onUpdateRemovedTeam(Team team, CallbackInfo ci) {
        doOnScoreboardModify();
    }

    private void doOnScoreboardModify() {
        ServerScoreboard scoreboard = (ServerScoreboard) (Object) this;
        ServerOnTeamsModify.EVENT.invoker().onScoreboardModify(scoreboard, this.server);
    }
}
