package net.azureaaron.mod.compatibility;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final boolean OPTIFABRIC_LOADED = FabricLoader.getInstance().isModLoaded("optifabric");
	private static final boolean SKYBLOCKER_LOADED = FabricLoader.getInstance().isModLoaded("skyblocker");
	private static final String MIXIN_PACKAGE = "net.azureaaron.mod.mixins.";

	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return switch (mixinClassName) {
			case String s when s.equals(MIXIN_PACKAGE + "KeyboardMixin") -> SystemUtils.IS_OS_MAC; //Skip application outside of macOS
			case String s when s.equals(MIXIN_PACKAGE + "ParticleManagerMixin") -> !OPTIFABRIC_LOADED;
			case String s when s.equals(MIXIN_PACKAGE + "FireworksSparkParticleMixin") -> !OPTIFABRIC_LOADED;
			case String s when s.equals(MIXIN_PACKAGE + "SkyblockerPVCollectionsGenericCategoryMixin") -> SKYBLOCKER_LOADED;
			case String s when s.equals(MIXIN_PACKAGE + "GlResourceManagerMixin") -> SystemUtils.IS_OS_MAC && "aarch64".equalsIgnoreCase(SystemUtils.OS_ARCH);

			default -> true;
		};
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
