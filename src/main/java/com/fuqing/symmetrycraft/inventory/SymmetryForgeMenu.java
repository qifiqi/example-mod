package com.fuqing.symmetrycraft.inventory;

import com.fuqing.symmetrycraft.blockentity.SymmetryForgeBlockEntity;
import com.fuqing.symmetrycraft.registry.ModItems;
import com.fuqing.symmetrycraft.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 对称锻炉菜单。
 *
 * 中轴对称的三槽布局（GUI 中围绕 x=88 完全镜像）：
 *   [材料 62,47] —— [水晶 80,47] —— [镜像 98,47]
 *
 * 机制：1 份材料 + 1 枚对称水晶 → 取出 2 份镜像成品。
 * 输出槽显示的是「预览」：条件满足时自动填入，取出时才真正消耗材料与水晶。
 */
public class SymmetryForgeMenu extends AbstractContainerMenu implements ContainerListener {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_CRYSTAL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int FORGE_SLOT_COUNT = 3;

    @Nullable
    private final SymmetryForgeBlockEntity forge;
    private final SimpleContainer forgeInventory;
    private boolean refreshingPreview;

    /** 服务端：直接持有方块实体。 */
    public SymmetryForgeMenu(int containerId, Inventory playerInventory, SymmetryForgeBlockEntity forge) {
        super(ModMenuTypes.SYMMETRY_FORGE.get(), containerId);
        this.forge = forge;
        this.forgeInventory = forge.getInventory();
        initSlots(playerInventory);
    }

    /** 客户端：从网络数据中的坐标找回方块实体。 */
    public SymmetryForgeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        super(ModMenuTypes.SYMMETRY_FORGE.get(), containerId);
        BlockPos pos = extraData.readBlockPos();
        this.forge = playerInventory.player.level().getBlockEntity(pos) instanceof SymmetryForgeBlockEntity existing
                ? existing
                : null;
        this.forgeInventory = this.forge != null ? this.forge.getInventory() : new SimpleContainer(FORGE_SLOT_COUNT);
        initSlots(playerInventory);
    }

    private void initSlots(Inventory playerInventory) {
        this.forgeInventory.addListener(this);

        // 对称三槽：材料 | 水晶 | 镜像（镜像于面板中轴 x=88）
        this.addSlot(new Slot(forgeInventory, SLOT_INPUT, 62, 47));
        this.addSlot(new CrystalSlot(forgeInventory, SLOT_CRYSTAL, 80, 47));
        this.addSlot(new MirrorSlot(forgeInventory, SLOT_OUTPUT, 98, 47));

        // 玩家背包 3 行 + 快捷栏
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, 9 + row * 9 + col, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        refreshPreview();
    }

    @Override
    public boolean stillValid(Player player) {
        return forge != null && forge.stillUsable(player);
    }

    @Override
    public void containerChanged(Container container) {
        // 锻炉容器内容变化时刷新输出槽预览
        refreshPreview();
    }

    /**
     * 刷新输出槽预览：材料 + 水晶齐备时展示镜像成品；条件被破坏时清空。
     * recursive guard：setItem 会回调 slotsChanged，这里用 refreshingPreview 折断递归。
     */
    private void refreshPreview() {
        if (refreshingPreview) {
            return;
        }
        refreshingPreview = true;
        try {
            ItemStack input = forgeInventory.getItem(SLOT_INPUT);
            ItemStack crystal = forgeInventory.getItem(SLOT_CRYSTAL);
            ItemStack current = forgeInventory.getItem(SLOT_OUTPUT);

            boolean ready = !input.isEmpty() && !crystal.isEmpty() && input.isStackable();
            if (ready) {
                if (current.isEmpty() || !ItemStack.isSameItemSameTags(current, input)) {
                    forgeInventory.setItem(SLOT_OUTPUT, mirrorOf(input));
                }
            } else if (!current.isEmpty()) {
                forgeInventory.removeItem(SLOT_OUTPUT, current.getCount());
            }
        } finally {
            refreshingPreview = false;
        }
    }

    private static ItemStack mirrorOf(ItemStack input) {
        ItemStack mirrored = input.copy();
        mirrored.setCount(2);
        return mirrored;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < FORGE_SLOT_COUNT) {
            // 锻炉槽 → 背包
            if (!this.moveItemStackTo(stack, FORGE_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(ModItems.SYMMETRY_CRYSTAL.get())) {
            // 水晶优先进入水晶槽，放不下则作为材料
            if (!this.moveItemStackTo(stack, SLOT_CRYSTAL, SLOT_CRYSTAL + 1, false)
                    && !this.moveItemStackTo(stack, SLOT_INPUT, SLOT_OUTPUT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // 其他物品 → 材料槽
            if (!this.moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    /** 只接受对称水晶的槽位。 */
    private static class CrystalSlot extends Slot {
        CrystalSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ModItems.SYMMETRY_CRYSTAL.get());
        }
    }

    /** 输出槽：只许取出，取出时消耗 1 材料 + 1 水晶。 */
    private static class MirrorSlot extends Slot {
        MirrorSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public void onTake(Player player, ItemStack taken) {
            // Forge 已把 Slot.onTake 提升为 public；this.container 是槽位持有的容器
            this.container.removeItem(SymmetryForgeBlockEntity.SLOT_INPUT, 1);
            this.container.removeItem(SymmetryForgeBlockEntity.SLOT_CRYSTAL, 1);
            this.container.setChanged();
        }
    }
}
