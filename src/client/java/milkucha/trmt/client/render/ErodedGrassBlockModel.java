package milkucha.trmt.client.render;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

/**
 * FabricBakedModel wrapper for ErodedGrassBlock block-state models.
 * Applies CUTOUT material to every non-DOWN quad so that transparent pixels in the
 * eroded-top overlay and the grass_block_side_overlay are discarded correctly.
 * Stage texture selection and FACING Y-rotation are already baked into the wrapped
 * model by the block-state system, so this class needs no per-position lookups.
 */
public class ErodedGrassBlockModel implements BakedModel {

    private final BakedModel wrapped;
    private static RenderMaterial cutoutMaterial;

    private static RenderMaterial cutoutMaterial() {
        if (cutoutMaterial == null) {
            cutoutMaterial = RendererAccess.INSTANCE.getRenderer()
                    .materialFinder()
                    .blendMode(BlendMode.CUTOUT)
                    .find();
        }
        return cutoutMaterial;
    }

    public ErodedGrassBlockModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isVanillaAdapter() { return false; }

    @Override
    public void emitBlockQuads(BlockAndTintGetter world, BlockState state, BlockPos pos,
                               Supplier<RandomSource> randomSupplier,
                               RenderContext context) {
        context.pushTransform(quad -> {
            if (quad.nominalFace() != Direction.DOWN) {
                quad.material(cutoutMaterial());
            }
            return true;
        });
        wrapped.emitBlockQuads(world, state, pos, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier,
                               RenderContext context) {
        wrapped.emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face,
                                    RandomSource random) {
        return wrapped.getQuads(state, face, random);
    }

    @Override public boolean useAmbientOcclusion() { return true; }
    @Override public boolean isGui3d()              { return wrapped.isGui3d(); }
    @Override public boolean usesBlockLight()       { return wrapped.usesBlockLight(); }
    @Override public boolean isCustomRenderer()     { return wrapped.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return wrapped.getParticleIcon(); }
    @Override public ItemTransforms getTransforms() { return wrapped.getTransforms(); }
    @Override public ItemOverrides getOverrides()   { return wrapped.getOverrides(); }
}
