package com.fuqing.symmetrycraft.client;

import com.fuqing.symmetrycraft.config.OptimizerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

/**
 * 对称 HUD：屏幕顶部居中，左右等距的「FPS ◆ 粒子」性能胶囊。
 * 边框上下左右完全对称，文字以中轴菱形为界镜像分布。
 */
public final class SymmetryHud {

    public static void render(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) {
            return;
        }
        if (!OptimizerConfig.SHOW_HUD.get()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.options.renderDebug) {
            return;
        }

        GuiGraphics gfx = event.getGuiGraphics();
        Font font = mc.font;

        String fpsText = "FPS " + mc.getFps();
        String particleText = "粒子 " + FpsOptimizer.getLiveParticles();

        int pad = 6;
        int gap = 14;
        int w1 = font.width(fpsText);
        int w2 = font.width(particleText);
        int totalWidth = pad + w1 + gap + w2 + pad;
        int x = (gfx.guiWidth() - totalWidth) / 2;
        int y = 4;
        int h = 13;

        // 胶囊底 + 四边完全对称的描边
        gfx.fill(x, y, x + totalWidth, y + h, 0x900D1B2A);
        gfx.fill(x, y, x + totalWidth, y + 1, 0xFF3FCFFF);
        gfx.fill(x, y + h - 1, x + totalWidth, y + h, 0xFF3FCFFF);
        gfx.fill(x, y, x + 1, y + h, 0xFF3FCFFF);
        gfx.fill(x + totalWidth - 1, y, x + totalWidth, y + h, 0xFF3FCFFF);

        // 左右文字关于中轴菱形对称分布
        gfx.drawString(font, fpsText, x + pad, y + 3, 0xE8F6FF);
        int centerX = x + totalWidth / 2;
        gfx.drawCenteredString(font, "◆", centerX, y + 3, 0x3FCFFF);
        gfx.drawString(font, particleText, x + totalWidth - pad - w2, y + 3, 0xE8F6FF);
    }

    private SymmetryHud() {
    }
}
