
package net.knifick.praporupdate.world.features;

import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SimpleRandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.WorldGenLevel;

import net.knifick.praporupdate.procedures.SoulSpawnConditionProcedure;

public class NetherFeatherFeature extends SimpleRandomSelectorFeature {
	public NetherFeatherFeature() {
		super(SimpleRandomFeatureConfiguration.CODEC);
	}

	public boolean place(FeaturePlaceContext<SimpleRandomFeatureConfiguration> context) {
		WorldGenLevel world = context.level();
		int x = context.origin().getX();
		int y = context.origin().getY();
		int z = context.origin().getZ();
		if (!SoulSpawnConditionProcedure.execute(world))
			return false;
		return super.place(context);
	}
}
