package com.github.alexthe666.iceandfire.item;

import java.util.function.Predicate;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.misc.IafTagRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemDragonBow extends BowItem implements ICustomRendered {
    public static final Predicate<ItemStack> DRAGON_ARROWS = (p_220002_0_) -> {
        ITag<Item> tag = ItemTags.getCollection().get(  IafTagRegistry.DRAGON_ARROWS);
        return p_220002_0_.getItem().isIn(tag);
    };

    public ItemDragonBow() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxDamage(584));
        this.setRegistryName(IceAndFire.MODID, "dragonbone_bow");
    }

    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return DRAGON_ARROWS;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        // NOTE: tweakbsd added, make the DragonBow ~15% faster than a regular bow
        return (int)Math.ceil((double)super.getUseDuration(stack) * 0.85D);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {

        System.out.println("DragonBow onPlayerStoppedUsing");

        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entityLiving;
            boolean flag = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = playerentity.findAmmo(stack);

            int i = (int) ((getUseDuration(stack) - timeLeft) * chargeVelocityMultiplier()); // NOTE: tweakbsd added from Botania - velocity multiplier
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, playerentity, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {

                    //itemstack = new ItemStack(Items.ARROW);
                    // NOTE: tweakbsd fixed usage of normal Arrow item with this bow when infinity is used.
                    itemstack = new ItemStack(IafItemRegistry.DRAGONBONE_ARROW);
                }

                float f = getArrowVelocity(i);
                if (!((double)f < 0.1D)) {
                    boolean flag1 = playerentity.abilities.isCreativeMode || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, stack, playerentity));
                    if (!worldIn.isRemote) {
                        ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, itemstack, playerentity);


                        System.out.println("DragonBow from ArrowItem class: " + arrowitem.getClass().toString());
                        System.out.println("DragonBow created arrow entity class: " + abstractarrowentity.getClass().toString());
                        System.out.println("DragonBow arrow entity damage: " + abstractarrowentity.getDamage());


                        abstractarrowentity = customArrow(abstractarrowentity);

                        System.out.println("DragonBow arrow entity damage after customArrow: " + abstractarrowentity.getDamage());

                        abstractarrowentity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F) {
                            abstractarrowentity.setIsCritical(true);
                        }

                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (j > 0) {
                            abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double)j * 0.5D + 0.5D);
                        }
                        System.out.println("DragonBoneArrow final damage: " + abstractarrowentity.getDamage());

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (k > 0) {
                            abstractarrowentity.setKnockbackStrength(k);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            abstractarrowentity.setFire(100);
                        }

                        stack.damageItem(1, playerentity, (p_220009_1_) -> {
                            p_220009_1_.sendBreakAnimation(playerentity.getActiveHand());
                        });
                        if (flag1 || playerentity.abilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                            abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }

                        worldIn.addEntity(abstractarrowentity);
                    }

                    worldIn.playSound((PlayerEntity)null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !playerentity.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            playerentity.inventory.deleteStack(itemstack);
                        }
                    }

                    playerentity.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        boolean flag = !playerIn.findAmmo(itemstack).isEmpty();

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, flag);
        if (ret != null) return ret;

        if (!playerIn.abilities.isCreativeMode && !flag) {
            return ActionResult.resultFail(itemstack);
        } else {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }


    @Override
    public boolean getIsRepairable(ItemStack bow, ItemStack material) {
        return material.getItem() == IafItemRegistry.DRAGON_BONE || super.getIsRepairable(bow, material);
    }

    public float chargeVelocityMultiplier() {
        return 1F;
    }

}
