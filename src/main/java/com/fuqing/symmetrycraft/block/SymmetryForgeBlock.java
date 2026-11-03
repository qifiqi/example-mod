package com.fuqing.symmetrycraft.block;

import com.fuqing.symmetrycraft.blockentity.SymmetryForgeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * 对称锻炉：外观左右镜像，交互打开对称工作界面。
 */
public class SymmetryForgeBlock extends BaseEntityBlock {

    public SymmetryForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SymmetryForgeBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SymmetryForgeBlockEntity forge && player instanceof ServerPlayer serverPlayer) {
                // BlockPos 重载会把坐标写进网络缓冲，与客户端菜单构造函数的 readBlockPos 对应
                NetworkHooks.openScreen(serverPlayer, forge, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SymmetryForgeBlockEntity forge) {
                forge.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
