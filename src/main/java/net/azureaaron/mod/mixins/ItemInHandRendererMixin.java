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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.azureaaron.mod.config.AaronModConfigManager;
import net.azureaaron.mod.config.configs.ItemModelConfig.AbstractHand;
import net.azureaaron.mod.screens.itemmodel.CustomizeItemModelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "renderArmWithItem", at = @At("HEAD"))
	private void aaronMod$changeVariablesForPreviewScreen(CallbackInfo ci, @Local(argsOnly = true) LocalRef<InteractionHand> hand, @Local(argsOnly = true, ordinal = 2) LocalFloatRef swingProgress, @Local LocalRef<ItemStack> stack, @Local(argsOnly = true, ordinal = 3) LocalFloatRef equipProgress, @Local(argsOnly = true) LocalIntRef light) {
		if (this.minecraft.screen instanceof CustomizeItemModelScreen itemModelScreen) {
			hand.set(itemModelScreen.hand);
			swingProgress.set(0f);
			stack.set(itemModelScreen.previewItem);
			equipProgress.set(0f);
			light.set(LightTexture.FULL_BRIGHT);
		}
	}

	@Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"))
	private void aaronMod$transformHandheldItem(CallbackInfo ci, @Local(argsOnly = true) InteractionHand hand, @Local(argsOnly = true) PoseStack matrices) {
		if (AaronModConfigManager.get().itemModel.enableItemModelCustomization) {
			AbstractHand config = switch (hand) {
				case MAIN_HAND -> AaronModConfigManager.get().itemModel.mainHand;
				case OFF_HAND -> AaronModConfigManager.get().itemModel.offHand;
			};

			if (!config.enabled) return;

			if (config.xRotation != 0 || config.yRotation != 0 || config.zRotation != 0) {
				matrices.mulPose(Axis.XP.rotationDegrees(config.xRotation));
				matrices.mulPose(Axis.YP.rotationDegrees(config.yRotation));
				matrices.mulPose(Axis.ZP.rotationDegrees(config.zRotation));
			}

			if (config.scale != 1f) {
				matrices.scale(config.scale, config.scale, config.scale);
			}

			if (config.x != 0 || config.y != 0 || config.z != 0) {
				matrices.translate((config.x / 100f) / config.scale, (config.y / 100f) / config.scale, (config.z / 100f) / config.scale);
			}
		}
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemSwapScale(F)F"))
	private float aaronMod$preventSwingAnimationBobbing(float original) {
		return AaronModConfigManager.get().itemModel.enableItemModelCustomization ? 1f : original;
	}
}
