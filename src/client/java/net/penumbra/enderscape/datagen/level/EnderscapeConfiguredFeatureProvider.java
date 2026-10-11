package net.penumbra.enderscape.datagen.level;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.concurrent.CompletableFuture;

import static net.penumbra.enderscape.registry.level.EnderscapeConfiguredFeatures.FEATURES;

public class EnderscapeConfiguredFeatureProvider extends FabricDynamicRegistryProvider {

    public EnderscapeConfiguredFeatureProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        FEATURES.forEach((key) -> add(provider, entries, key));
    }

    private void add(HolderLookup.Provider provider, Entries entries, ResourceKey<Feature> key) {
        entries.add(key, provider.lookupOrThrow(Registries.FEATURE).getOrThrow(key).value());
    }

    @Override
    public String getName() {
        return "Configured Features";
    }
}
