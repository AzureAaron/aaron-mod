package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.cbyrne.betterinject.annotations.Arg;
import dev.cbyrne.betterinject.annotations.Inject;
import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.FpsDisplay;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"), 
    		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 0)))
    private int aaronMod$shadowEntryAndTitleText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color, Operation<Integer> operation) {
    	return Config.shadowedScoreboard ? textRenderer.drawWithShadow(matrices, text, x, y, color) : operation.call(textRenderer, matrices, text, x, y, color);
    }
        
    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I", ordinal = 0))
    private int aaronMod$shadowScoreText(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color, Operation<Integer> operation) {
    	return Config.shadowedScoreboard ? textRenderer.drawWithShadow(matrices, text, x, y, color) : operation.call(textRenderer, matrices, text, x, y, color);
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    public void aaronMod$fpsDisplay(@Arg MatrixStack matrices) {
    	if(Config.fpsDisplay) FpsDisplay.render(matrices);
    }
}
