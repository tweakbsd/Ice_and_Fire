package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;

import com.github.alexthe666.iceandfire.item.DragonsteelArmorMaterial;
import com.github.alexthe666.iceandfire.item.ItemDragonsteelArmor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockIceSpikes extends Block {
    protected static final VoxelShape AABB = Block.makeCuboidShape(1, 0, 1, 15, 8, 15);
    public Item itemBlock;

    public BlockIceSpikes() {
        super(
    		Properties
    			.create(Material.PACKED_ICE)
    			.notSolid()
    			.variableOpacity()
    			.tickRandomly()
    			.sound(SoundType.GLASS)
    			.hardnessAndResistance(2.5F)
    			.harvestLevel(1)
    			.harvestTool(ToolType.PICKAXE)
    			.setRequiresTool()
		);

        this.setRegistryName(IceAndFire.MODID, "dragon_ice_spikes");
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    private boolean isValidGround(BlockState blockState, IWorldReader worldIn, BlockPos blockpos) {
        return blockState.isSolid();
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {


        boolean hasIceDragonProtection = false;
        if(entityIn instanceof EntityIceDragon) {
            hasIceDragonProtection = true;
        } else if(entityIn instanceof MobEntity) {

            MobEntity mobEntity = (MobEntity)entityIn;
            for (ItemStack itemStack: mobEntity.getArmorInventoryList()) {
                if(itemStack.isEmpty()) {
                    continue;
                }

                Item anItem = itemStack.getItem();
                if(anItem instanceof ItemDragonsteelArmor) {
                    ItemDragonsteelArmor dragonsteelArmor = (ItemDragonsteelArmor)anItem;
                    if(dragonsteelArmor.getEquipmentSlot() == EquipmentSlotType.FEET) {
                        IArmorMaterial armorMaterial = dragonsteelArmor.getArmorMaterial();
                        if(armorMaterial instanceof DragonsteelArmorMaterial) {
                            if(((DragonsteelArmorMaterial)armorMaterial).getName().equals("dragonsteel_ice")) {
                                hasIceDragonProtection = true;
                                break;
                            }

                        }
                    }
                }

            }

        }

        if(!(hasIceDragonProtection || entityIn.isInvulnerableTo(DamageSource.CACTUS))) {

            if(entityIn.attackEntityFrom(DamageSource.CACTUS, 1)) {
                if (entityIn instanceof LivingEntity && entityIn.getMotion().x != 0 && entityIn.getMotion().z != 0) {
                    ((LivingEntity) entityIn).applyKnockback(0.5F, entityIn.getMotion().x, entityIn.getMotion().z);
                }
            }
        }

    }

    public boolean isTransparent(BlockState state) {
        return true;
    }

}
