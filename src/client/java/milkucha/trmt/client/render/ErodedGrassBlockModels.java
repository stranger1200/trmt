package milkucha.trmt.client.render;

public final class ErodedGrassBlockModels {

    private ErodedGrassBlockModels() {}

    public static void register() {
        // TODO(26.1): Re-implement custom BakedModel wrapping for cutout grass overlay.
        // The pre-26.1 implementation used Fabric's renderer-api-v1 (RendererAccess,
        // BlendMode, RenderMaterial, RenderContext) plus FabricBakedModel. Those APIs
        // were removed in the 26.1 client rendering rework. Until a 26.1-compatible
        // path is wired up, the eroded grass block uses its vanilla blockstate model
        // with a JSON-side render_type=cutout_mipped (declared in the model files).
    }
}
