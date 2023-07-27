package net.azureaaron.mod.features;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.azureaaron.mod.Config;
import net.azureaaron.mod.features.BoundingBoxes.Dragons;
import net.azureaaron.mod.util.Cache;
import net.azureaaron.mod.util.Functions;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DragonTimers {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Vec3d POWER_TEXT_LOCATION = new Vec3d(26, 16, 59); //26 6 59
	private static final Vec3d FLAME_TEXT_LOCATION = new Vec3d(86, 16, 56); //86 6 56
	private static final Vec3d APEX_TEXT_LOCATION = new Vec3d(26, 16, 94); //26 6 94
	private static final Vec3d ICE_TEXT_LOCATION = new Vec3d(85, 16, 94); //85 6 94
	private static final Vec3d SOUL_TEXT_LOCATION = new Vec3d(56, 16, 126); //56 8 126
	
	public static void renderSpawnTimers(WorldRenderContext wrc) {
		if(Functions.isOnHypixel() && Config.m7DragonSpawnTimers && Cache.inM7Phase5) {
			if(Cache.powerSpawnStart != 0L && Cache.powerSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.powerSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				renderTextInWorld(wrc, POWER_TEXT_LOCATION, spawnText);
			}
			
			if(Cache.flameSpawnStart != 0L && Cache.flameSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.flameSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				renderTextInWorld(wrc, FLAME_TEXT_LOCATION, spawnText);
			}
			
			if(Cache.apexSpawnStart != 0L && Cache.apexSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.apexSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				renderTextInWorld(wrc, APEX_TEXT_LOCATION, spawnText);
			}
			
			if(Cache.iceSpawnStart != 0L && Cache.iceSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.iceSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				renderTextInWorld(wrc, ICE_TEXT_LOCATION, spawnText);
			}
			
			if(Cache.soulSpawnStart != 0L && Cache.soulSpawnStart + 5000 > System.currentTimeMillis()) {
				int timeUntilSpawn = (int) (Cache.soulSpawnStart + 5000 - System.currentTimeMillis());
				OrderedText spawnText = Text.literal(timeUntilSpawn + " ms").asOrderedText();
				renderTextInWorld(wrc, SOUL_TEXT_LOCATION, spawnText);
			}
		}
	}
	
	public static void tick(ParticleS2CPacket packet) {
		if(Functions.isOnHypixel() && Cache.inM7Phase5 && packet.getParameters().getType().equals(ParticleTypes.ENCHANT)) {
			for(Dragons dragon : Dragons.values()) {
				String dragonName = dragon.name();
				int xShrinkFactor = (dragon.pos1.getX() == 41) ? 11 : 0;
				int zShrinkFactor = (dragon.pos1.getZ() == 112) ? 0 : 11;
				Box box = new Box(dragon.pos1.add(0, 14, 0), dragon.pos2).contract(xShrinkFactor, 0, zShrinkFactor);
				
				if(box.contains(packet.getX(), packet.getY(), packet.getZ())) {
					if(dragonName.equals("POWER") && Cache.powerSpawnStart + 5000 < System.currentTimeMillis()) Cache.powerSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("FLAME") && Cache.flameSpawnStart + 5000 < System.currentTimeMillis()) Cache.flameSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("APEX") && Cache.apexSpawnStart + 5000 < System.currentTimeMillis()) Cache.apexSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("ICE") && Cache.iceSpawnStart + 5000 < System.currentTimeMillis()) Cache.iceSpawnStart = System.currentTimeMillis();
					if(dragonName.equals("SOUL") && Cache.soulSpawnStart + 5000 < System.currentTimeMillis()) Cache.soulSpawnStart = System.currentTimeMillis();
				}
			}
		}
	}

	private static void renderTextInWorld(WorldRenderContext wrc, Vec3d targetPosition, OrderedText text) {
		Vec3d camera = CLIENT.getCameraEntity().getCameraPosVec(wrc.tickDelta());
		MatrixStack matrices = wrc.matrixStack();
		TextRenderer textRenderer = CLIENT.textRenderer;
		
		Vec3d transformedPosition = targetPosition.subtract(camera);
		
		matrices.push();
		matrices.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);
		matrices.peek().getPositionMatrix().mul(RenderSystem.getModelViewMatrix());
		matrices.multiply(wrc.camera().getRotation());
		matrices.scale(-0.2f, -0.2f, 0.2f);
		
		Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
		float h = -textRenderer.getWidth(text) / 2f;
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder buffer = tessellator.getBuffer();
		VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(buffer);
		
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		
		textRenderer.draw(text, h, 0, 0xFFFFFFFF, false, positionMatrix, consumers, TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		consumers.draw();
		matrices.pop();
	}
}
