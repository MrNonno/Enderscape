package net.penumbra.enderscape.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.penumbra.enderscape.block.properties.DirectionSet;

public class CelestialPathBlock extends AbstractOvergrowthBlock {

    public CelestialPathBlock(BlockBehaviour.Properties settings) {
        super(true, Blocks.END_STONE, DirectionSet.create().up(), settings);
    }

}