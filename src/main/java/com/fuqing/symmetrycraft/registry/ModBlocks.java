package com.fuqing.symmetrycraft.registry;

import com.fuqing.symmetrycraft.SymmetryCraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SymmetryCraft.MOD_ID);

    /** 对称水晶块：四枚水晶的镜像沉淀，中心微微发光。 */
    public static final RegistryObject<Block> SYMMETRY_CRYSTAL_BLOCK = BLOCKS.register("symmetry_crystal_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIAMOND)
                    .strength(3.0F, 8.0F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 5)));

    /** 对称锻炉：左右镜像的锻炉核心，打开对称工作界面。 */
    public static final RegistryObject<Block> SYMMETRY_FORGE = BLOCKS.register("symmetry_forge",
            () -> new com.fuqing.symmetrycraft.block.SymmetryForgeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 7)));

    private ModBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
