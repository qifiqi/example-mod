package com.fuqing.symmetrycraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * 按键绑定：默认 O 键打开「对称优化设置」。
 */
public final class KeyBindings {
    public static final KeyMapping OPEN_OPTIMIZER = new KeyMapping(
            "key.symmetrycraft.open_optimizer",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.symmetrycraft");

    private KeyBindings() {
    }
}
