package net.penumbra.enderscape.datagen.level;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import java.util.concurrent.CompletableFuture;

import static net.penumbra.enderscape.registry.level.EnderscapeSurfaceRules.MATERIAL_RULES;

public class EnderscapeMaterialRulesProvider extends FabricDynamicRegistryProvider {

    public EnderscapeMaterialRulesProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        MATERIAL_RULES.forEach((key) -> add(provider, entries, key));
    }

    private void add(HolderLookup.Provider provider, Entries entries, ResourceKey<MaterialRule> key) {
        entries.add(key, provider.lookupOrThrow(Registries.MATERIAL_RULE).getOrThrow(key).value());
    }

    @Override
    public String getName() {
        return "MaterialRules";
    }
}
