package milkucha.trmt;

import milkucha.trmt.block.ErodedDirtBlock;
import milkucha.trmt.block.ErodedGrassBlock;
import milkucha.trmt.block.ErodedSandBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public final class TRMTBlocks {

    public static final Block ERODED_DIRT = register(
            "eroded_dirt",
            ErodedDirtBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).randomTicks()
    );

    public static final Block ERODED_COARSE_DIRT = register(
            "eroded_coarse_dirt",
            ErodedDirtBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).randomTicks()
    );

    public static final Block ERODED_GRASS_BLOCK = register(
            "eroded_grass_block",
            ErodedGrassBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).mapColor(MapColor.DIRT).randomTicks()
    );

    public static final Block ERODED_SAND = register(
            "eroded_sand",
            ErodedSandBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_YELLOW).noOcclusion().randomTicks()
    );

    private TRMTBlocks() {}

    public static void register() {}

    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("trmt", name));
        T block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
