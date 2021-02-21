package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.ClientMumbleLinkMod;
import com.skaggsm.mumblelinkmod.ClientMumbleLinkModKt;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {

	@Inject(method = "openToLan", at = @At(value = "HEAD"))
	public void openToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> ci) {
		ClientMumbleLinkModKt.setOpenedLan(true);
		ClientMumbleLinkMod.INSTANCE.onInitializeClient();
	}

}
