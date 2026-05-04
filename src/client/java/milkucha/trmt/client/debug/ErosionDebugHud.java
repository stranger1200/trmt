package milkucha.trmt.client.debug;

public class ErosionDebugHud {

    private ErosionDebugHud() {}

    public static void register() {
        // TODO(26.1): Re-implement debug HUD overlay against the new HUD layer API.
        // The pre-26.1 implementation hooked Fabric's HudRenderCallback (removed in
        // 26.1) and rendered baked-quad sprites for the 5-cell compass-cross around
        // the player. The 26.1 HUD pipeline switched to a layered-draw model and
        // BakedModel.getQuads no longer exists; rewriting needs the new
        // GuiLayer/HudLayerRegistrationCallback flow + the new model API.
    }
}
