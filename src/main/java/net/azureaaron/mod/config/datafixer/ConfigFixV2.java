package net.azureaaron.mod.config.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class ConfigFixV2 extends DataFix {

	public ConfigFixV2(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
				"ConfigFixV2",
				this.getInputSchema().getType(ConfigDataFixer.CONFIG_TYPE),
				configTyped -> configTyped.update(DSL.remainderFinder(), this::fix)
				);
	}

	private Dynamic<?> fix(Dynamic<?> dynamic) {
		return fixItemModelCustomization(ConfigDataFixerUtils.updateVersion(dynamic, this));
	}

	private Dynamic<?> fixItemModelCustomization(Dynamic<?> dynamic) {
		return dynamic.update("itemModel", itemModel -> itemModel
				.update("mainHand", this::fixPosition)
				.update("offHand", this::fixPosition));
	}

	private Dynamic<?> fixPosition(Dynamic<?> hand) {
		float scale = hand.get("scale").asFloat(1f);

		return hand.update("x", xDynamic -> recalibratePosition(xDynamic, scale))
				.update("y", yDynamic -> recalibratePosition(yDynamic, scale))
				.update("z", zDynamic -> recalibratePosition(zDynamic, scale));
	}

	private Dynamic<?> recalibratePosition(Dynamic<?> position, float scale) {
		return position.createFloat(position.asFloat(0f) * scale * 100f);
	}
}
