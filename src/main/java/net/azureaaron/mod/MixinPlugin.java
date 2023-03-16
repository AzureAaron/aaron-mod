package net.azureaaron.mod;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final boolean OPTIFABRIC_LOADED = FabricLoader.getInstance().isModLoaded("optifabric");
	private static final String MIXIN_PACKAGE = "net.azureaaron.mod.mixins.";
	private static final boolean USE_BETTER_MATH = Boolean.parseBoolean(System.getProperty("aaronmod.useBetterMath", "false")) && !OPTIFABRIC_LOADED;

	@Override
	public void onLoad(String mixinPackage) {		
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		//Only apply if we're on macOS to avoid unnecessary bytecode transformation.
		if(mixinClassName.equals(MIXIN_PACKAGE + "KeyboardMixin")) return SystemUtils.IS_OS_MAC;
		
		//OptiFine compatibility
		if(OPTIFABRIC_LOADED && (mixinClassName.equals(MIXIN_PACKAGE + "ParticleManagerMixin") || mixinClassName.equals(MIXIN_PACKAGE + "FireworksSparkParticleMixin"))) return false;
		
		//Better Math Toggle
		if(!USE_BETTER_MATH) {
			switch(mixinClassName) {
			case MIXIN_PACKAGE + "MathHelperMixin": return false;
			case MIXIN_PACKAGE + "Vec3dMixin": return false;
			case MIXIN_PACKAGE + "Vec3iMixin": return false;
			case MIXIN_PACKAGE + "Vec2fMixin": return false;
			}
		}
		
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {		
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {		
	}

}
