package milkucha.trmt.client.debug;

import milkucha.trmt.TRMTBlocks;
import milkucha.trmt.block.ErodedGrassBlock;
import milkucha.trmt.block.ErodedSandBlock;
import milkucha.trmt.client.TRMTClientConfig;
import milkucha.trmt.client.network.ClientErosionCache;
import milkucha.trmt.erosion.BlockThresholds;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Text-only debug HUD showing erosion stats for the 5-block compass-cross
 * around the block under the player. Toggle via TRMTClientConfig.debugHud.
 */
public class ErosionDebugHud {

    private static final Identifier ID = Identifier.fromNamespaceAndPath("trmt", "erosion_debug");

    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int DATA_COLOR  = 0xFFAAFFFF;
    private static final int MARGIN      = 4;
    private static final int LINE_HEIGHT = 10;

    private static final Direction[] HORIZONTALS = {
        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private ErosionDebugHud() {}

    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, ID, ErosionDebugHud::extract);
    }

    private static void extract(GuiGraphicsExtractor ctx, DeltaTracker tickCounter) {
        if (!TRMTClientConfig.get().debugHud) return;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;

        Font font = client.font;
        ClientLevel world = client.level;
        BlockPos center = client.player.blockPosition().below();
        long now = world.getGameTime();

        int rows = 7;
        int x = MARGIN;
        int y = ctx.guiHeight() - MARGIN - rows * LINE_HEIGHT;

        ctx.text(font, "[TRMT Erosion]", x, y, LABEL_COLOR, true);
        renderRow(ctx, font, world, center.north(), "N", x, y + LINE_HEIGHT,     now);
        renderRow(ctx, font, world, center.west(),  "W", x, y + 2 * LINE_HEIGHT, now);
        renderRow(ctx, font, world, center,         "C", x, y + 3 * LINE_HEIGHT, now);
        renderRow(ctx, font, world, center.east(),  "E", x, y + 4 * LINE_HEIGHT, now);
        renderRow(ctx, font, world, center.south(), "S", x, y + 5 * LINE_HEIGHT, now);
        ctx.text(font,
                "@ " + center.getX() + " " + center.getY() + " " + center.getZ(),
                x, y + 6 * LINE_HEIGHT, LABEL_COLOR, true);
    }

    private static void renderRow(GuiGraphicsExtractor ctx, Font font,
                                   ClientLevel world, BlockPos pos, String label,
                                   int x, int y, long now) {
        BlockState state = world.getBlockState(pos);
        ClientErosionCache.Entry entry = ClientErosionCache.getInstance().getEntry(pos);

        StringBuilder line = new StringBuilder();
        line.append(label).append(": ");
        if (entry != null) {
            line.append(String.format("%.1f/%.1f age:%d", entry.walkedOnCount, entry.threshold,
                    now - entry.lastTouchedGameTime));
        } else {
            line.append("-/- age:-");
        }

        long timeout = resolveTimeout(state);
        if (timeout < 0) {
            line.append(" out:-");
        } else {
            boolean isolated = isIsolatedClient(world, pos);
            if (isolated) timeout /= 2;
            line.append(" out:").append(timeout).append(isolated ? " I" : "");
        }

        ctx.text(font, line.toString(), x, y, DATA_COLOR, true);
    }

    private static long resolveTimeout(BlockState state) {
        Block block = state.getBlock();
        if (block == TRMTBlocks.ERODED_GRASS_BLOCK) {
            return BlockThresholds.getGrassDeErosionTimeout(state.getValue(ErodedGrassBlock.STAGE) + 1);
        }
        if (block == TRMTBlocks.ERODED_DIRT || block == TRMTBlocks.ERODED_COARSE_DIRT) {
            return BlockThresholds.getDirtDeErosionTimeout(block);
        }
        if (block == TRMTBlocks.ERODED_SAND) {
            return BlockThresholds.getSandDeErosionTimeout(state.getValue(ErodedSandBlock.STAGE));
        }
        return -1;
    }

    private static boolean isIsolatedClient(ClientLevel world, BlockPos pos) {
        for (Direction dir : HORIZONTALS) {
            for (int dy = -1; dy <= 1; dy++) {
                BlockPos neighbor = pos.relative(dir).above(dy);
                Block neighborBlock = world.getBlockState(neighbor).getBlock();
                if (neighborBlock == TRMTBlocks.ERODED_GRASS_BLOCK
                        || neighborBlock == TRMTBlocks.ERODED_DIRT
                        || neighborBlock == TRMTBlocks.ERODED_COARSE_DIRT
                        || neighborBlock == TRMTBlocks.ERODED_SAND) {
                    return false;
                }
            }
        }
        return true;
    }
}
