package com.fuqing.symmetrycraft.client;

import com.fuqing.symmetrycraft.SymmetryCraft;
import com.fuqing.symmetrycraft.inventory.SymmetryForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 对称锻炉界面：独立绘制的 GUI 材质。
 * 三个槽位与标签围绕面板中轴（x=88）完全镜像对称。
 */
public class SymmetryForgeScreen extends AbstractContainerScreen<SymmetryForgeMenu> {
    private static final ResourceLocation TEXTURE = SymmetryCraft.id("textures/gui/symmetry_forge.png");

    public SymmetryForgeScreen(SymmetryForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int centerX = this.imageWidth / 2;
        // 标题居中于对称轴
        graphics.drawCenteredString(this.font, this.title, centerX, 8, 0x3FCFFF);
        // 三个两字标签等宽对称：材料 · 水晶 · 镜像
        graphics.drawCenteredString(this.font, Component.translatable("gui.symmetrycraft.slot.input"), 70, 32, 0x55606E);
        graphics.drawCenteredString(this.font, Component.translatable("gui.symmetrycraft.slot.crystal"), 88, 32, 0x3FCFFF);
        graphics.drawCenteredString(this.font, Component.translatable("gui.symmetrycraft.slot.output"), 106, 32, 0x55606E);
        // 提示：1 材料 + 1 水晶 = 2 镜像
        graphics.drawCenteredString(this.font, Component.translatable("gui.symmetrycraft.hint"), centerX, 66, 0x55606E);
        // 背包标签居中（原版习惯是左对齐，这里保持中轴对称）
        graphics.drawCenteredString(this.font, Component.translatable("container.inventory"), centerX, 72, 0x3F3F3F);
    }
}
