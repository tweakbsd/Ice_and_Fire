package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;

/*
public class PixieAIPickupCakeItem<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected ItemEntity targetEntity;

    public PixieAIPickupItem(EntityPixie creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public PixieAIPickupItem(EntityPixie creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 20, checkSight, onlyNearby, null);
    }

    public PixieAIPickupItem(EntityPixie creature, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
        this.targetEntitySelector = new Predicate<ItemEntity>() {
            @Override
            public boolean apply(@Nullable ItemEntity item) {

                return item instanceof ItemEntity && !item.getItem().isEmpty() && (item.getItem().getItem() == Items.CAKE && !creature.isTamed() || item.getItem().getItem() == Items.SUGAR && creature.isTamed() && creature.getHealth() < creature.getMaxHealth());
            }
        };
    }

    @Override
    public boolean shouldExecute() {

        EntityPixie pixie = (EntityPixie)this.goalOwner;
        if(pixie.isSitting()) return false;

        List<ItemEntity> list = this.goalOwner.world.getEntitiesWithinAABB(ItemEntity.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);

        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            this.targetEntity = list.get(0);
            return true;
        }
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        // NOTE: Changed y to 4.0 just like AmphitereAITargetItems
        return this.goalOwner.getBoundingBox().grow(targetDistance, 4.0, targetDistance);
    }

    @Override
    public void startExecuting() {

        this.goalOwner.getMoveHelper().setMoveTo(this.targetEntity.getPosX(), this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 0.25D);
        // NOTE: tweakbsd changed code to be like AmphitereAITargetItems
        //this.goalOwner.getNavigator().tryMoveToXYZ(this.targetEntity.getPosX(), this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 0.25D);
        //EntityPixie pixie = (EntityPixie) this.goalOwner;


        LivingEntity attackTarget = this.goalOwner.getAttackTarget();
        System.out.println("PixieAIPickup.startExecuting() attackTarget: " + (attackTarget != null ? attackTarget.getDisplayName(): "null"));

        // NOTE: Why getAttackTarget() ???
        if (attackTarget == null) {
            this.goalOwner.getLookController().setLookPosition(this.targetEntity.getPosX(), this.targetEntity.getPosY(), this.targetEntity.getPosZ(), 180.0F, 20.0F);
        }
        super.startExecuting();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || this.targetEntity != null && !this.targetEntity.isAlive()) {
            this.resetTask();
        }

        System.out.println("PixieAIPickup.tick() targetEntity: " + (this.targetEntity != null ? this.targetEntity.getDisplayName() : "null"));

        if (this.targetEntity != null && this.targetEntity.isAlive() && this.goalOwner.getDistanceSq(this.targetEntity) < 1) {
            EntityPixie pixie = (EntityPixie) this.goalOwner;
            if (this.targetEntity.getItem() != null && this.targetEntity.getItem().getItem() != null && this.targetEntity.getItem().getItem() == Items.SUGAR) {
                pixie.heal(5);
            }
            if (this.targetEntity.getItem() != null && this.targetEntity.getItem().getItem() != null && this.targetEntity.getItem().getItem() == Items.CAKE) {
                if (!pixie.isTamed() && this.targetEntity.getThrowerId() != null && this.goalOwner.world.getPlayerByUuid(this.targetEntity.getThrowerId()) != null) {
                    PlayerEntity owner = this.goalOwner.world.getPlayerByUuid(this.targetEntity.getThrowerId());
                    pixie.setTamed(true);
                    pixie.setOwnerId(owner.getUniqueID());
                    // NOTE: setOnGround()
                    pixie.func_230245_c_(true);

                    // NOTE: Make sitting !
                    //pixie.func_233686_v_(true);
                }
            }

            pixie.setHeldItem(Hand.MAIN_HAND, this.targetEntity.getItem());
            //this.targetEntity.getItem().shrink(1);
            pixie.playSound(IafSoundRegistry.PIXIE_TAUNT, 1F, 1F);
            resetTask();
        }
    }

    @Override
    public boolean shouldContinueExecuting() {


        //this.goalOwner.setAttackTarget(this.target);

        return true;
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.getDistanceSq(p_compare_1_);
            double d1 = this.theEntity.getDistanceSq(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}

 */