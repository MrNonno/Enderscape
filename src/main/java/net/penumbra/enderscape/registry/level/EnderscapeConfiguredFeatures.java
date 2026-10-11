package net.penumbra.enderscape.registry.level;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.block.state.PurifyingPhase;
import net.penumbra.enderscape.block.state.StateProperties;
import net.penumbra.enderscape.feature.*;
import net.penumbra.enderscape.feature.ScatteredOreFeature;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import net.penumbra.enderscape.registry.tag.EnderscapeBlockTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnderscapeConfiguredFeatures {

    public static final List<ResourceKey<Feature>> FEATURES = new ArrayList<>();

    public static final ResourceKey<Feature> BLINKLIGHT_VINES = registerKey("blinklight_vines");
    public static final ResourceKey<Feature> BULB_FLOWER = registerKey("bulb_flower");
    public static final ResourceKey<Feature> CEILING_NEBULITE_ORE = registerKey("ceiling_nebulite_ore");
    public static final ResourceKey<Feature> CELESTIAL_GROVE_VEGETATION = registerKey("celestial_grove_vegetation");
    public static final ResourceKey<Feature> CELESTIAL_GROWTH = registerKey("celestial_growth");
    public static final ResourceKey<Feature> CHORUS_SPROUTS = registerKey("chorus_sprouts");
    public static final ResourceKey<Feature> CORRUPT_BARRENS_VEGETATION = registerKey("corrupt_barrens_vegetation");
    public static final ResourceKey<Feature> DOWNWARD_CORRUPT_GROWTH = registerKey("downward_corrupt_growth");
    public static final ResourceKey<Feature> DOWNWARD_LARGE_MURUBLIGHT_CHANTERELLE = registerKey("downward_large_murublight_chanterelle");
    public static final ResourceKey<Feature> DOWNWARD_REPULSIVE_MAGNIA_SPROUT = registerKey("downward_repulsive_magnia_sprout");
    public static final ResourceKey<Feature> DOWNWARD_TALL_CORRUPT_GROWTH = registerKey("downward_tall_corrupt_growth");
    public static final ResourceKey<Feature> DRY_END_GROWTH = registerKey("dry_end_growth");
    public static final ResourceKey<Feature> ENDERSCAPE_ISLAND = registerKey("enderscape_island");
    public static final ResourceKey<Feature> KURODITE = registerKey("kurodite");
    public static final ResourceKey<Feature> LARGE_CELESTIAL_CHANTERELLE = registerKey("large_celestial_chanterelle");
    public static final ResourceKey<Feature> LARGE_MAGNIA_SPIKE = registerKey("large_magnia_spike");
    public static final ResourceKey<Feature> MAGNIA_ARCH = registerKey("magnia_arch");
    public static final ResourceKey<Feature> MAGNIA_SPIKE = registerKey("magnia_spike");
    public static final ResourceKey<Feature> MAGNIA_TOWER = registerKey("magnia_tower");
    public static final ResourceKey<Feature> MIRESTONE_BLOB = registerKey("mirestone_blob");
    public static final ResourceKey<Feature> MIRESTONE_PILLARS = registerKey("mirestone_pillars");
    public static final ResourceKey<Feature> MURUBLIGHT_BRACKET = registerKey("murublight_bracket");
    public static final ResourceKey<Feature> NEBULITE_ORE = registerKey("nebulite_ore");
    public static final ResourceKey<Feature> SCATTERED_SHADOLINE_ORE = registerKey("scattered_shadoline_ore");
    public static final ResourceKey<Feature> SHADOLINE_ORE = registerKey("shadoline_ore");
    public static final ResourceKey<Feature> UPWARD_ALLURING_MAGNIA_SPROUT = registerKey("upward_alluring_magnia_sprout");
    public static final ResourceKey<Feature> UPWARD_CORRUPT_GROWTH = registerKey("upward_corrupt_growth");
    public static final ResourceKey<Feature> UPWARD_LARGE_MURUBLIGHT_CHANTERELLE = registerKey("upward_large_murublight_chanterelle");
    public static final ResourceKey<Feature> UPWARD_TALL_CORRUPT_GROWTH = registerKey("upward_tall_corrupt_growth");
    public static final ResourceKey<Feature> VEILED_TREE = registerKey("veiled_tree");
    public static final ResourceKey<Feature> VEILED_TREE_FROM_SAPLING = registerKey("veiled_tree_from_sapling");
    public static final ResourceKey<Feature> VEILED_WOODLANDS_VEGETATION = registerKey("veiled_woodlands_vegetation");
    public static final ResourceKey<Feature> VERADITE = registerKey("veradite");
    public static final ResourceKey<Feature> VOID_FIRE = registerKey("void_fire");
    public static final ResourceKey<Feature> VOID_LAKE = registerKey("void_lake");
    public static final ResourceKey<Feature> VOID_SHALE = registerKey("void_shale");
    public static final ResourceKey<Feature> VOID_SHALE_BLOB = registerKey("void_shale_blob");
    public static final ResourceKey<Feature> WISP_FLOWER = registerKey("wisp_flower");

    private static final List<BlockReplacement> KURODITE_TARGETS = List.of(
            new BlockReplacement(new TagMatchTest(EnderscapeBlockTags.ORE_REPLACEABLE), EnderscapeBlocks.KURODITE.defaultBlockState())
    );

    private static final List<BlockReplacement> NEBULITE_ORE_TARGETS = List.of(
            new BlockReplacement(new BlockMatchTest(Blocks.END_STONE), EnderscapeBlocks.NEBULITE_ORE.defaultBlockState()),
            new BlockReplacement(new BlockMatchTest(EnderscapeBlocks.MIRESTONE), EnderscapeBlocks.MIRESTONE_NEBULITE_ORE.defaultBlockState())
    );

    private static final List<BlockReplacement> SHADOLINE_ORE_TARGETS = List.of(
            new BlockReplacement(new BlockMatchTest(Blocks.END_STONE), EnderscapeBlocks.SHADOLINE_ORE.defaultBlockState()),
            new BlockReplacement(new BlockMatchTest(EnderscapeBlocks.MIRESTONE), EnderscapeBlocks.MIRESTONE_SHADOLINE_ORE.defaultBlockState())
    );

    private static final List<BlockReplacement> VERADITE_TARGETS = List.of(
            new BlockReplacement(new TagMatchTest(EnderscapeBlockTags.ORE_REPLACEABLE), EnderscapeBlocks.VERADITE.defaultBlockState())
    );

    private static final List<BlockReplacement> VOID_SHALE_TARGETS = List.of(
            new BlockReplacement(new BlockMatchTest(EnderscapeBlocks.MIRESTONE), EnderscapeBlocks.VOID_SHALE.defaultBlockState())
    );

    private static final List<BlockReplacement> VOID_SHALE_BLOBS_TARGETS = List.of(
            new BlockReplacement(new TagMatchTest(EnderscapeBlockTags.ORE_REPLACEABLE), EnderscapeBlocks.VOID_SHALE.defaultBlockState())
    );

    private static final List<BlockReplacement> MIRESTONE_BLOB_TARGETS = List.of(
            new BlockReplacement(new BlockMatchTest(Blocks.END_STONE), EnderscapeBlocks.MIRESTONE.defaultBlockState())
    );

    public static void bootstrap(BootstrapContext<Feature> context) {
        registerVeiledWoodlands(context);
        registerMagniaCrags(context);
        registerCelestialGrove(context);
        registerCorruptBarrens(context);

        context.register(ENDERSCAPE_ISLAND, new EnderscapeIslandFeature());

        context.register(CHORUS_SPROUTS, new SimpleBlockFeature(BlockStateProvider.of(EnderscapeBlocks.CHORUS_SPROUTS)));
        context.register(DRY_END_GROWTH, new SimpleBlockFeature(BlockStateProvider.of(EnderscapeBlocks.DRY_END_GROWTH)));
        context.register(MURUBLIGHT_BRACKET, new MurublightBracketFeature());

        context.register(NEBULITE_ORE, new OreFeature(NEBULITE_ORE_TARGETS, 4));
        context.register(CEILING_NEBULITE_ORE, new CeilingOreFeature(NEBULITE_ORE_TARGETS, ConstantInt.of(8)));

        context.register(SHADOLINE_ORE, new OreFeature(SHADOLINE_ORE_TARGETS, 10));
        context.register(SCATTERED_SHADOLINE_ORE, new ScatteredOreFeature(SHADOLINE_ORE_TARGETS, UniformInt.of(70, 90), UniformInt.of(-1, 1), ConstantInt.of(1)));

        context.register(VERADITE, new OreFeature(VERADITE_TARGETS, 28));

        context.register(VOID_FIRE, new SimpleBlockFeature(BlockStateProvider.of(EnderscapeBlocks.VOID_FIRE)));
        context.register(VOID_LAKE, VoidLakeFeature.DEFAULT);
        context.register(VOID_SHALE, new VoidShaleFeature(VOID_SHALE_TARGETS, ConstantInt.of(5)));
        context.register(VOID_SHALE_BLOB, new OreFeature(VOID_SHALE_BLOBS_TARGETS, 16, 1));

        context.register(MIRESTONE_BLOB, new OreFeature(MIRESTONE_BLOB_TARGETS, 16, 0.5F));
    }

    private static void registerVeiledWoodlands(BootstrapContext<Feature> context) {

        context.register(VEILED_WOODLANDS_VEGETATION, new SimpleBlockFeature(
                new WeightedStateProvider(new WeightedList.Builder<BlockState>()
                        .add(EnderscapeBlocks.WISP_SPROUTS.defaultBlockState(), 4)
                        .add(EnderscapeBlocks.WISP_GROWTH.defaultBlockState(), 1)
                )
        ));

        context.register(WISP_FLOWER, new SimpleBlockFeature(BlockStateProvider.of(EnderscapeBlocks.WISP_FLOWER)));

        context.register(VEILED_TREE, new VeiledTreeFeature(
                UniformInt.of(1, 2),
                UniformInt.of(3, 4),
                ConstantInt.of(3),
                UniformInt.of(4, 5),
                ConstantFloat.of(0.6F),
                Optional.of(new GrowthFeature(
                        EnderscapeBlocks.VEILED_VINES.defaultBlockState(),
                        UniformInt.of(1, 2),
                        UniformInt.of(1, 6),
                        0.15F)
                ),
                Optional.of(new VeiledLeafPileFeature(
                        UniformFloat.of(6.0F, 10.0F),
                        ConstantFloat.of(0.2F),
                        UniformInt.of(1, 2)
                ))
        ));

        context.register(VEILED_TREE_FROM_SAPLING, new VeiledTreeFeature(
                UniformInt.of(1, 2),
                UniformInt.of(3, 4),
                ConstantInt.of(3),
                UniformInt.of(4, 5),
                ConstantFloat.of(0.6F),
                Optional.of(new GrowthFeature(
                        EnderscapeBlocks.VEILED_VINES.defaultBlockState(),
                        UniformInt.of(1, 2),
                        UniformInt.of(1, 6),
                        0.15F)
                ),
                Optional.empty()
        ));
    }

    private static void registerMagniaCrags(BootstrapContext<Feature> context) {
        context.register(UPWARD_ALLURING_MAGNIA_SPROUT, new SimpleBlockFeature(BlockStateProvider.of(EnderscapeBlocks.ALLURING_MAGNIA_SPROUT)));

        context.register(DOWNWARD_REPULSIVE_MAGNIA_SPROUT,
                new BlockColumnFeature(List.of(
                        BlockColumnFeature.layer(
                                ConstantInt.of(1),
                                BlockStateProvider.of(EnderscapeBlocks.REPULSIVE_MAGNIA_SPROUT.defaultBlockState().setValue(StateProperties.FACING, Direction.DOWN))
                        )
                ), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));

        context.register(MAGNIA_ARCH, new MagniaArchFeature());

        context.register(MAGNIA_TOWER, new MagniaTowerFeature(
                UniformFloat.of(6.0F, 8.0F),
                UniformInt.of(12, 18),
                ConstantInt.of(3),
                ConstantInt.of(7),
                ConstantFloat.of(0.025F),
                ConstantFloat.of(0.05F))
        );

        context.register(MAGNIA_SPIKE, new MagniaSpikeFeature(
                UniformInt.of(3, 8),
                ConstantFloat.of(0.05F),
                2)
        );

        context.register(LARGE_MAGNIA_SPIKE, new MagniaSpikeFeature(
                UniformInt.of(6, 12),
                ConstantFloat.of(0.05F),
                6)
        );
    }

    private static void registerCelestialGrove(BootstrapContext<Feature> context) {
        BlockState poweredBulbFlower = PurifyingPhase.with(EnderscapeBlocks.BULB_FLOWER, PurifyingPhase.INACTIVE);

        context.register(BULB_FLOWER, new SimpleBlockFeature(BlockStateProvider.of(poweredBulbFlower)));
        context.register(CELESTIAL_GROVE_VEGETATION, new SimpleBlockFeature(
                new WeightedStateProvider(new WeightedList.Builder<BlockState>()
                        .add(EnderscapeBlocks.CELESTIAL_CHANTERELLE.defaultBlockState(), 8)
                        .add(poweredBulbFlower, 1)
                )
        ));
        context.register(CELESTIAL_GROWTH, new GrowthFeature(EnderscapeBlocks.CELESTIAL_GROWTH.defaultBlockState(), UniformInt.of(1, 2), UniformInt.of(1, 3), 0.125F));
        context.register(LARGE_CELESTIAL_CHANTERELLE, new LargeCelestialChanterelleFeature(UniformInt.of(10, 35), 4, 0.75F, 1, 64, 16));
    }

    private static void registerCorruptBarrens(BootstrapContext<Feature> context) {
        context.register(MIRESTONE_PILLARS, BlockColumnFeature.simple(BiasedToBottomInt.of(1, 4), BlockStateProvider.of(EnderscapeBlocks.MIRESTONE)));

        context.register(KURODITE, new OreFeature(KURODITE_TARGETS, 28));

        context.register(BLINKLIGHT_VINES,
                new BlockColumnFeature(List.of(
                        BlockColumnFeature.layer(UniformInt.of(4, 15), BlockStateProvider.of(EnderscapeBlocks.BLINKLIGHT_VINES_BODY)),
                        BlockColumnFeature.layer(ConstantInt.of(1), BlockStateProvider.of(EnderscapeBlocks.BLINKLIGHT_VINES_HEAD.defaultBlockState().setValue(StateProperties.AGE_25, 25)))
                ), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));

        context.register(CORRUPT_BARRENS_VEGETATION, new SimpleBlockFeature(
                new WeightedStateProvider(new WeightedList.Builder<BlockState>()
                        .add(EnderscapeBlocks.MURUBLIGHT_CHANTERELLE.defaultBlockState(), 1)
                ))
        );

        context.register(UPWARD_CORRUPT_GROWTH, new GrowthFeature(EnderscapeBlocks.CORRUPT_GROWTH.defaultBlockState().setValue(StateProperties.FACING, Direction.UP), ConstantInt.of(1), UniformInt.of(0, 1), 0.3F));
        context.register(UPWARD_TALL_CORRUPT_GROWTH, new GrowthFeature(EnderscapeBlocks.CORRUPT_GROWTH.defaultBlockState().setValue(StateProperties.FACING, Direction.UP), UniformInt.of(3, 4), ConstantInt.of(0), 0.0F));

        context.register(DOWNWARD_CORRUPT_GROWTH, new GrowthFeature(EnderscapeBlocks.CORRUPT_GROWTH.defaultBlockState().setValue(StateProperties.FACING, Direction.DOWN), ConstantInt.of(1), UniformInt.of(1, 2), 0.5F));
        context.register(DOWNWARD_TALL_CORRUPT_GROWTH, new GrowthFeature(EnderscapeBlocks.CORRUPT_GROWTH.defaultBlockState().setValue(StateProperties.FACING, Direction.DOWN), UniformInt.of(4, 8), ConstantInt.of(0), 0.0F));

        context.register(DOWNWARD_LARGE_MURUBLIGHT_CHANTERELLE, new LargeMurublightChanterelleFeature(Direction.DOWN, UniformInt.of(15, 30), UniformInt.of(4, 6), 32));
        context.register(UPWARD_LARGE_MURUBLIGHT_CHANTERELLE, new LargeMurublightChanterelleFeature(Direction.UP, UniformInt.of(15, 30), UniformInt.of(4, 6), 32));
    }

    private static ResourceKey<Feature> registerKey(String name) {
        ResourceKey<Feature> key = ResourceKey.create(Registries.FEATURE, Enderscape.id(name));
        FEATURES.add(key);
        return key;
    }
}
