package com.github.alexthe666.iceandfire.entity.ai;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.block.BlockMyrmexCocoon;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityMyrmexCocoon;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MyrmexAIStoreItems extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private BlockPos nextRoom = null;
    private BlockPos nextCocoon = null;
    private BlockPos mainRoom = null;
    private boolean first = true; //first stage - enter the main hive room then storage room

    public MyrmexAIStoreItems(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex instanceof EntityMyrmexWorker && ((EntityMyrmexWorker) this.myrmex).holdingBaby() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigator().noPath() || this.myrmex.canSeeSky() || this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            return false;
        }
        if (this.myrmex.getWaitTicks()>0){
            this.myrmex.setWaitTicks(this.myrmex.getWaitTicks()-1);
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            return false;
        } else {
            first = true;
            mainRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getCenter());
            nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(WorldGenMyrmexHive.RoomType.FOOD, this.myrmex.getRNG(), this.myrmex.getPosition()));
            nextCocoon = getNearbyCocoon(nextRoom);
            if(nextCocoon == null){
                this.myrmex.setWaitTicks(new Random().nextInt(40));
            }
            return nextCocoon != null;
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty() && nextCocoon != null && isUseableCocoon(nextCocoon) && this.myrmex.getDistanceSq(nextCocoon.getX() + 0.5D, nextCocoon.getY() + 0.5D, nextCocoon.getZ() + 0.5D) > 3 && this.myrmex.shouldEnterHive();
    }

    @Override
    public void tick() {
        if (first && mainRoom != null) {
            this.myrmex.getNavigator().tryMoveToXYZ(mainRoom.getX() + 0.5D, mainRoom.getY() + 0.5D, mainRoom.getZ() + 0.5D, this.movementSpeed);
            if (this.myrmex.getDistanceSq(mainRoom.getX() + 0.5D, mainRoom.getY() + 0.5D, mainRoom.getZ() + 0.5D) < 10D) {
                first = false;
                return;
            }
        }
        if (!first && nextCocoon != null) {
            if(myrmex.getNavigator().noPath()){
                this.myrmex.getNavigator().tryMoveToXYZ(nextCocoon.getX() + 0.5D, nextCocoon.getY() + 0.5D, nextCocoon.getZ() + 0.5D, this.movementSpeed);
            }
            double dist = 3 * 3;
            if (this.myrmex.getDistanceSq(nextCocoon.getX() + 0.5D, nextCocoon.getY() + 0.5D, nextCocoon.getZ() + 0.5D) < dist && !this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty() && isUseableCocoon(nextCocoon)) {
                TileEntityMyrmexCocoon cocoon = (TileEntityMyrmexCocoon) this.myrmex.world.getTileEntity(nextCocoon);
                ItemStack itemstack = this.myrmex.getHeldItem(Hand.MAIN_HAND);
                if (!itemstack.isEmpty()) {
                    for (int i = 0; i < cocoon.getSizeInventory(); ++i) {
                        if (!itemstack.isEmpty()) {
                            ItemStack cocoonStack = cocoon.getStackInSlot(i);
                            if (cocoonStack.isEmpty()) {
                                cocoon.setInventorySlotContents(i, itemstack.copy());
                                cocoon.markDirty();

                                this.myrmex.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                this.myrmex.isEnteringHive = false;
                                return;
                            }else if (cocoonStack.getItem() == itemstack.getItem()) {
                                int j = Math.min(cocoon.getInventoryStackLimit(), cocoonStack.getMaxStackSize());
                                int k = Math.min(itemstack.getCount(), j - cocoonStack.getCount());
                                if (k > 0) {
                                    cocoonStack.grow(k);
                                    itemstack.shrink(k);

                                    if (itemstack.isEmpty()) {
                                        cocoon.markDirty();
                                    }
                                    this.myrmex.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                    this.myrmex.isEnteringHive = false;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void resetTask() {
        nextRoom = null;
        nextCocoon = null;
        mainRoom = null;
        first = true;
    }

    public BlockPos getNearbyCocoon(BlockPos roomCenter) {
        int RADIUS_XZ = 15;
        int RADIUS_Y = 7;
        List<BlockPos> closeCocoons = new ArrayList<BlockPos>();
        BlockPos.getAllInBox(roomCenter.add(-RADIUS_XZ, -RADIUS_Y, -RADIUS_XZ), roomCenter.add(RADIUS_XZ, RADIUS_Y, RADIUS_XZ)).forEach(blockpos -> {
            TileEntity te = this.myrmex.world.getTileEntity(blockpos);
            if (te != null && te instanceof TileEntityMyrmexCocoon) {
                if (!((TileEntityMyrmexCocoon) te).isFull(this.myrmex.getHeldItem(Hand.MAIN_HAND))) {
                    closeCocoons.add(blockpos);
                }
            }
        });
        if (closeCocoons.isEmpty()) {
            return null;
        }
        return closeCocoons.get(myrmex.getRNG().nextInt(Math.max(closeCocoons.size() - 1, 1)));
    }

    public boolean isUseableCocoon(BlockPos blockpos) {
        if (this.myrmex.world.getBlockState(blockpos).getBlock() instanceof BlockMyrmexCocoon && this.myrmex.world.getTileEntity(blockpos) != null && this.myrmex.world.getTileEntity(blockpos) instanceof TileEntityMyrmexCocoon) {
            return !((TileEntityMyrmexCocoon) this.myrmex.world.getTileEntity(blockpos)).isFull(this.myrmex.getHeldItem(Hand.MAIN_HAND));
        }
        return false;
    }
}