package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.ItemModelConfig.AbstractHand;
import net.azureaaron.mod.screens.itemmodel.CustomizeItemModelScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
	private void aaronMod$changeVariablesForPreviewScreen(CallbackInfo ci, @Local(argsOnly = true) LocalRef<Hand> hand, @Local(argsOnly = true, ordinal = 2) LocalFloatRef swingProgress, @Local LocalRef<ItemStack> stack, @Local(argsOnly = true, ordinal = 3) LocalFloatRef equipProgress, @Local(argsOnly = true) LocalIntRef light) {
		if (this.client.currentScreen instanceof CustomizeItemModelScreen itemModelScreen) {
			hand.set(itemModelScreen.hand);
			swingProgress.set(0f);
			stack.set(itemModelScreen.previewItem);
			equipProgress.set(0f);
			light.set(LightmapTextureManager.MAX_LIGHT_COORDINATE);
		}
	}

	@Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"))
	private void aaronMod$transformHandheldItem(CallbackInfo ci, @Local(argsOnly = true) Hand hand, @Local(argsOnly = true) MatrixStack matrices) {
		if (AaronModConfigManager.get().itemModel.enableItemModelCustomization) {
			AbstractHand config = switch (hand) {
				case MAIN_HAND -> AaronModConfigManager.get().itemModel.mainHand;
				case OFF_HAND -> AaronModConfigManager.get().itemModel.offHand;
			};

			if (!config.enabled) return;

			if (config.xRotation != 0 || config.yRotation != 0 || config.zRotation != 0) {
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(config.xRotation));
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(config.yRotation));
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(config.zRotation));
			}

			if (config.scale != 1f) {
				matrices.scale(config.scale, config.scale, config.scale);
			}

			if (config.x != 0 || config.y != 0 || config.z != 0) {
				matrices.translate((config.x / 100f) / config.scale, (config.y / 100f) / config.scale, (config.z / 100f) / config.scale);
			}
		}
	}

	@ModifyExpressionValue(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
	private float aaronMod$preventSwingAnimationBobbing(float original) {
		return AaronModConfigManager.get().itemModel.enableItemModelCustomization ? 1f : original;
	}
}
