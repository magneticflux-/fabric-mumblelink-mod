package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.ServerOnConnectCallback;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */
@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onPlayerConnect(ClientConnection clientConnection_1, ServerPlayerEntity serverPlayerEntity_1, CallbackInfo ci) {
        ServerOnConnectCallback.EVENT.invoker().onConnect(serverPlayerEntity_1);
    }
}
