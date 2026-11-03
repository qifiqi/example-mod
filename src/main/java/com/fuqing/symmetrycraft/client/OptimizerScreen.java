package com.fuqing.symmetrycraft.client;

import com.fuqing.symmetrycraft.config.OptimizerConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 对称优化设置界面（默认按 O 打开）。
 *
 * 布局围绕屏幕中轴完全对称：
 *   三枚预设按钮等距横排 → 三条全宽滑条 → 左右镜像的双开关 → 左右镜像的「恢复默认 | 完成」。
 */
public class OptimizerScreen extends Screen {
    private static final int PANEL_HALF_WIDTH = 136;
    private static final int FIELD_WIDTH = 248;
    private static final int HALF_FIELD_WIDTH = 120;
    private static final int PRESET_WIDTH = 78;
    private static final int PRESET_GAP = 8;
    private static final int BUTTON_HEIGHT = 20;

    private static final int[] PRESET_LOW = {12, 800, 24, 1, 1};
    private static final int[] PRESET_BALANCED = {32, 4096, 64, 0, 1};
    private static final int[] PRESET_HIGH = {64, 8192, 128, 0, 1};

    private int panelBottom;

    public OptimizerScreen() {
        super(Component.translatable("gui.symmetrycraft.optimizer.title"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int fieldX = cx - FIELD_WIDTH / 2;

        // —— 预设行：低配 | 均衡 | 高配，等距对称 ——
        int presetY = 50;
        addRenderableWidget(Button.builder(Component.translatable("gui.symmetrycraft.presets.low"),
                        b -> applyPreset(PRESET_LOW))
                .bounds(cx - PRESET_GAP / 2 - PRESET_WIDTH - PRESET_WIDTH / 2, presetY, PRESET_WIDTH, BUTTON_HEIGHT).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.symmetrycraft.presets.balanced"),
                        b -> applyPreset(PRESET_BALANCED))
                .bounds(cx - PRESET_WIDTH / 2, presetY, PRESET_WIDTH, BUTTON_HEIGHT).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.symmetrycraft.presets.high"),
                        b -> applyPreset(PRESET_HIGH))
                .bounds(cx + PRESET_GAP / 2 + PRESET_WIDTH / 2, presetY, PRESET_WIDTH, BUTTON_HEIGHT).build());

        // —— 三条全宽滑条，水平居中 ——
        addRenderableWidget(new OptimizerSlider(fieldX, presetY + 28, FIELD_WIDTH, BUTTON_HEIGHT,
                "gui.symmetrycraft.option.particle_distance", OptimizerConfig.PARTICLE_CULL_DISTANCE, 8, 128));
        addRenderableWidget(new OptimizerSlider(fieldX, presetY + 52, FIELD_WIDTH, BUTTON_HEIGHT,
                "gui.symmetrycraft.option.particle_cap", OptimizerConfig.MAX_PARTICLES, 256, 8192));
        addRenderableWidget(new OptimizerSlider(fieldX, presetY + 76, FIELD_WIDTH, BUTTON_HEIGHT,
                "gui.symmetrycraft.option.mob_distance", OptimizerConfig.MOB_RENDER_DISTANCE, 16, 128));

        // —— 双开关：左右镜像 ——
        int toggleY = presetY + 104;
        addRenderableWidget(toggleButton(fieldX, toggleY, HALF_FIELD_WIDTH,
                "gui.symmetrycraft.option.reduce_fog", OptimizerConfig.REDUCE_FOG));
        addRenderableWidget(toggleButton(cx + FIELD_WIDTH / 2 - HALF_FIELD_WIDTH, toggleY, HALF_FIELD_WIDTH,
                "gui.symmetrycraft.option.show_hud", OptimizerConfig.SHOW_HUD));

        // —— 底部：恢复默认 | 完成，左右镜像 ——
        int bottomY = toggleY + 28;
        addRenderableWidget(Button.builder(Component.translatable("gui.symmetrycraft.reset"),
                        b -> applyPreset(PRESET_BALANCED))
                .bounds(fieldX, bottomY, HALF_FIELD_WIDTH, BUTTON_HEIGHT).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(cx + FIELD_WIDTH / 2 - HALF_FIELD_WIDTH, bottomY, HALF_FIELD_WIDTH, BUTTON_HEIGHT).build());

        this.panelBottom = bottomY + BUTTON_HEIGHT + 10;
    }

    private void applyPreset(int[] preset) {
        OptimizerConfig.PARTICLE_CULL_DISTANCE.set(preset[0]);
        OptimizerConfig.MAX_PARTICLES.set(preset[1]);
        OptimizerConfig.MOB_RENDER_DISTANCE.set(preset[2]);
        OptimizerConfig.REDUCE_FOG.set(preset[3] == 1);
        OptimizerConfig.SHOW_HUD.set(preset[4] == 1);
        this.rebuildWidgets();
    }

    private static Button toggleButton(int x, int y, int width, String key, ForgeConfigSpec.BooleanValue setting) {
        Button button = Button.builder(Component.empty(), b -> {
            setting.set(!setting.get());
            b.setMessage(toggleMessage(key, setting.get()));
        }).bounds(x, y, width, BUTTON_HEIGHT).build();
        button.setMessage(toggleMessage(key, setting.get()));
        return button;
    }

    private static Component toggleMessage(String key, boolean value) {
        return Component.translatable(key)
                .append(": ")
                .append(Component.translatable(value ? "gui.symmetrycraft.on" : "gui.symmetrycraft.off"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int cx = this.width / 2;
        this.renderBackground(graphics);

        // 对称面板：上下青色描边、左右紫色描边，四角收口
        int x0 = cx - PANEL_HALF_WIDTH;
        int x1 = cx + PANEL_HALF_WIDTH;
        graphics.fill(x0, 22, x1, this.panelBottom + 24, 0xE8101826);
        graphics.fill(x0, 22, x1, 23, 0xFF3FCFFF);
        graphics.fill(x0, this.panelBottom + 23, x1, this.panelBottom + 24, 0xFF3FCFFF);
        graphics.fill(x0, 22, x0 + 1, this.panelBottom + 24, 0xFF7B5CFF);
        graphics.fill(x1 - 1, 22, x1, this.panelBottom + 24, 0xFF7B5CFF);
        // 顶部中央菱形装饰（与 HUD 呼应）
        graphics.drawCenteredString(this.font, "◆", cx, 30, 0x3FCFFF);

        graphics.drawCenteredString(this.font, this.title, cx, 41, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);

        // 底部统计行：本会话累计剔除的粒子数
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.symmetrycraft.stats", FpsOptimizer.getCulledParticles()),
                cx, this.panelBottom + 10, 0x7B8794);
    }

    /** 数值滑条：拖动即写入配置，消息实时显示当前值。 */
    private static class OptimizerSlider extends AbstractSliderButton {
        private final String labelKey;
        private final ForgeConfigSpec.IntValue setting;
        private final int min;
        private final int max;

        OptimizerSlider(int x, int y, int width, int height, String labelKey,
                        ForgeConfigSpec.IntValue setting, int min, int max) {
            super(x, y, width, height, Component.empty(), normalize(setting.get(), min, max));
            this.labelKey = labelKey;
            this.setting = setting;
            this.min = min;
            this.max = max;
            updateMessage();
        }

        private static double normalize(int value, int min, int max) {
            return Mth.clamp((double) (value - min) / (double) (max - min), 0.0D, 1.0D);
        }

        private int currentValue() {
            return this.min + (int) Math.round(this.value * (double) (this.max - this.min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable(this.labelKey, this.currentValue()));
        }

        @Override
        protected void applyValue() {
            this.setting.set(this.currentValue());
        }
    }
}
