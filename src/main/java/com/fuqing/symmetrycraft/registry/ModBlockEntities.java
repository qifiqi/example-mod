package com.fuqing.symmetrycraft.registry;

import com.fuqing.symmetrycraft.SymmetryCraft;
import com.fuqing.symmetrycraft.blockentity.SymmetryForgeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SymmetryCraft.MOD_ID);

    public static final RegistryObject<BlockEntityType<SymmetryForgeBlockEntity>> SYMMETRY_FORGE =
            BLOCK_ENTITIES.register("symmetry_forge",
                    () -> BlockEntityType.Builder.of(SymmetryForgeBlockEntity::new, ModBlocks.SYMMETRY_FORGE.get())
                            .build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}
