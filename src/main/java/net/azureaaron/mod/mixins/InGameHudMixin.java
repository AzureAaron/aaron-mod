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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    
    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"), 
    		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0)))
    private int aaronMod$shadowEntryAndTitleText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int colour, boolean shadow, Operation<Integer> operation) {
    	return Config.shadowedScoreboard ? context.drawTextWithShadow(textRenderer, text, x, y, colour) : operation.call(context, textRenderer, text, x, y, colour, shadow);
    }
    
    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I", ordinal = 0))
    private int aaronMod$shadowScoreText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int colour, boolean shadow, Operation<Integer> operation) {
    	return Config.shadowedScoreboard ? context.drawTextWithShadow(textRenderer, text, x, y, colour) : operation.call(context, textRenderer, text, x, y, colour, shadow);
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    public void aaronMod$fpsDisplay(@Arg DrawContext context) {
    	if(Config.fpsDisplay) FpsDisplay.render(context);
    }
}
