package net.penumbra.enderscape.registry.level;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.feature.*;

public abstract class EnderscapeFeatures {
    private static <T extends Feature> void register(String name, MapCodec<T> entry) {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, Enderscape.id(name), entry);
    }
    
    static {
        register("ceiling_ore", CeilingOreFeature.CODEC);
        register("enderscape_island", EnderscapeIslandFeature.CODEC);
        register("growth", GrowthFeature.CODEC);
        register("large_celestial_chanterelle", LargeCelestialChanterelleFeature.CODEC);
        register("large_murublight_chanterelle", LargeMurublightChanterelleFeature.CODEC);
        register("murublight_bracket", MurublightBracketFeature.CODEC);
        register("magnia_arch", MagniaArchFeature.CODEC);
        register("magnia_tower", MagniaTowerFeature.CODEC);
        register("magnia_spike", MagniaSpikeFeature.CODEC);
        register("scattered_ore", ScatteredOreFeature.CODEC);
        register("veiled_leaf_pile", VeiledLeafPileFeature.CODEC);
        register("veiled_tree",VeiledTreeFeature.CODEC);
        register("void_lake", VoidLakeFeature.CODEC);
        register("void_shale", VoidShaleFeature.CODEC);
    }
}