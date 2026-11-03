package com.fuqing.symmetrycraft.blockentity;

import com.fuqing.symmetrycraft.inventory.SymmetryForgeMenu;
import com.fuqing.symmetrycraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 对称锻炉的方块实体：三个槽位（材料 | 水晶 | 镜像）持久化保存。
 */
public class SymmetryForgeBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_CRYSTAL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int CONTAINER_SIZE = 3;

    private final SimpleContainer inventory = new SimpleContainer(CONTAINER_SIZE);

    public SymmetryForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYMMETRY_FORGE.get(), pos, state);
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public boolean stillUsable(Player player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(
                        this.worldPosition.getX() + 0.5D,
                        this.worldPosition.getY() + 0.5D,
                        this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        SimpleContainer copy = new SimpleContainer(CONTAINER_SIZE);
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            copy.setItem(i, inventory.getItem(i));
        }
        Containers.dropContents(this.level, this.worldPosition, copy);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.symmetrycraft.symmetry_forge");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SymmetryForgeMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", inventory.createTag());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.fromTag(tag.getList("Items", Tag.TAG_COMPOUND));
    }
}
