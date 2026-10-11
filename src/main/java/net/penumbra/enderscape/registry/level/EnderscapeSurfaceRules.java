package net.penumbra.enderscape.registry.level;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.BootstrapContextAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.block.state.StateProperties;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.data.worldgen.material.VanillaMaterialConditions.ON_FLOOR;
import static net.minecraft.world.level.levelgen.material.MaterialRules.*;

public class EnderscapeSurfaceRules {
    public static final List<ResourceKey<MaterialRule>> MATERIAL_RULES = new ArrayList<>();

    public static final ResourceKey<MaterialRule> END = createKey("end");

    private static ResourceKey<MaterialRule> createKey(String string) {
        ResourceKey<MaterialRule> key = ResourceKey.create(Registries.MATERIAL_RULE, Enderscape.id(string));
        MATERIAL_RULES.add(key);
        return key;
    }

    @NotNull
    private static MaterialRule getDefault(Block block) {
        return state(block.defaultBlockState());
    }

    private static final MaterialRule ALLURING_MAGNIA = getDefault(EnderscapeBlocks.ALLURING_MAGNIA);
    private static final MaterialRule REPULSIVE_MAGNIA = getDefault(EnderscapeBlocks.REPULSIVE_MAGNIA);
    private static final MaterialRule VEILED_END_STONE = getDefault(EnderscapeBlocks.VEILED_END_STONE);
    private static final MaterialRule CELESTIAL_OVERGROWTH = getDefault(EnderscapeBlocks.CELESTIAL_OVERGROWTH);
    private static final MaterialRule MIRESTONE = getDefault(EnderscapeBlocks.MIRESTONE);

    private static final MaterialRule CORRUPT_OVERGROWTH_FACING_DOWN = state(EnderscapeBlocks.CORRUPT_OVERGROWTH.defaultBlockState().setValue(StateProperties.FACING, Direction.DOWN));
    private static final MaterialRule CORRUPT_OVERGROWTH_FACING_UP = state(EnderscapeBlocks.CORRUPT_OVERGROWTH.defaultBlockState().setValue(StateProperties.FACING, Direction.UP));

    public static final double WIDE_MAGNIA_SURFACE_NOISE_MAXIMUM = 0.075;
    public static final double THIN_MAGNIA_SURFACE_NOISE_MAXIMUM = 0.02;

    public static final int MAGNIA_DEPTH = 2;
    public static final int MIRESTONE_DEPTH = 4;

    public static void bootstrap(BootstrapContext<MaterialRule> context) {
        context.register(END, sequence(
                sequence(
                        magniaCrags(context),
                        corruptBarrens(context),
                        mirestoneUndersides()
                ),
                veiledWoodlands(context),
                celestialGrove(context),
                voidDepths(context)
        ));
    }

    @NotNull
    private static MaterialRule mirestoneUndersides() {
        return ifTrue(
                stoneDepthCheck(0, true, MIRESTONE_DEPTH, CaveSurface.CEILING),
                MIRESTONE
        );
    }

    @NotNull
    private static MaterialRule veiledWoodlands(BootstrapContextAccess provider) {
        return ifTrue(
                isBiome(provider.lookup(Registries.BIOME), EnderscapeBiomes.VEILED_WOODLANDS),
                ifTrue(
                        condition(provider, ON_FLOOR),
                        ifTrue(
                                noiseCondition2d(EnderscapeNoiseParameters.VEILED_SURFACE, -0.5, 0.5),
                                VEILED_END_STONE
                        )
                )
        );
    }

    @NotNull
    private static MaterialRule magniaCrags(BootstrapContextAccess provider) {
        return ifTrue(
                isBiome(provider.lookup(Registries.BIOME), EnderscapeBiomes.MAGNIA_FIELDS),
                sequence(
                        ifTrue(
                                stoneDepthCheck(0, true, MAGNIA_DEPTH, CaveSurface.FLOOR),
                                sequence(
                                        ifTrue(
                                                noiseCondition2d(Noises.SURFACE, -WIDE_MAGNIA_SURFACE_NOISE_MAXIMUM, WIDE_MAGNIA_SURFACE_NOISE_MAXIMUM),
                                                ALLURING_MAGNIA
                                        ),
                                        ifTrue(
                                                noiseCondition2d(Noises.CALCITE, -THIN_MAGNIA_SURFACE_NOISE_MAXIMUM, THIN_MAGNIA_SURFACE_NOISE_MAXIMUM),
                                                ALLURING_MAGNIA
                                        )
                                )
                        ),
                        ifTrue(
                                stoneDepthCheck(0, true, MAGNIA_DEPTH, CaveSurface.CEILING),
                                sequence(
                                        ifTrue(
                                                noiseCondition2d(Noises.SURFACE, -WIDE_MAGNIA_SURFACE_NOISE_MAXIMUM, WIDE_MAGNIA_SURFACE_NOISE_MAXIMUM),
                                                REPULSIVE_MAGNIA
                                        ),
                                        ifTrue(
                                                noiseCondition2d(Noises.CALCITE, -THIN_MAGNIA_SURFACE_NOISE_MAXIMUM, THIN_MAGNIA_SURFACE_NOISE_MAXIMUM),
                                                REPULSIVE_MAGNIA
                                        )
                                )
                        )
                )
        );
    }

    @NotNull
    private static MaterialRule celestialGrove(BootstrapContextAccess provider) {
        return ifTrue(
                isBiome(provider.lookup(Registries.BIOME), EnderscapeBiomes.CELESTIAL_GROVE),
                ifTrue(
                        condition(provider, ON_FLOOR),
                        ifTrue(
                                noiseCondition2d(EnderscapeNoiseParameters.CELESTIAL_SURFACE, -0.7, 0),
                                CELESTIAL_OVERGROWTH
                        )
                )
        );
    }

    @NotNull
    private static MaterialRule corruptBarrens(BootstrapContextAccess provider) {
        return ifTrue(
                isBiome(provider.lookup(Registries.BIOME), EnderscapeBiomes.CORRUPT_BARRENS),
                sequence(
                        ifTrue(
                                condition(provider, ON_FLOOR),
                                ifTrue(
                                        noiseCondition2d(EnderscapeNoiseParameters.CORRUPTION_CEILING, -0.6D, 0.0D),
                                        CORRUPT_OVERGROWTH_FACING_UP
                                )
                        ),
                        ifTrue(
                                condition(provider, ON_FLOOR),
                                ifTrue(
                                        noiseCondition2d(EnderscapeNoiseParameters.CORRUPTION_CEILING, -1.0D, 0.0D),
                                        CORRUPT_OVERGROWTH_FACING_DOWN
                                )
                        ),
                        MIRESTONE
                )
        );
    }


    @NotNull
    private static MaterialRule voidDepths(BootstrapContextAccess provider) {
        return ifTrue(
                isBiome(provider.lookup(Registries.BIOME), EnderscapeBiomes.VOID_DEPTHS),
                ifTrue(
                        condition(provider, ON_FLOOR),
                        ifTrue(
                                noiseCondition2d(EnderscapeNoiseParameters.CORRUPTION_CEILING, -0.6D, 0.0D),
                                MIRESTONE
                        )
                )
        );
    }

    private static MaterialCondition condition(BootstrapContextAccess provider, ResourceKey<MaterialCondition> condition) {
        return MaterialRules.getCondition(provider.lookup(Registries.MATERIAL_CONDITION), condition);
    }
}