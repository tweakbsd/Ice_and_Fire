package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import com.github.alexthe666.iceandfire.item.ItemTideTrident;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Arrays;
import java.lang.reflect.Field;

public class EntityTideTrident extends TridentEntity {

    // NOTE: added by tweakbsd to match damage when used as item.
    public static float ATTACK_DAMAGE = ItemTideTrident.ATTACK_DAMAGE;
    public static int PIERCE_LEVEL = 2;

    public EntityTideTrident(EntityType type, World worldIn) {
        super(type, worldIn);
        thrownStack = new ItemStack(IafItemRegistry.TIDE_TRIDENT);
        this.setPierceLevel((byte)PIERCE_LEVEL);  // NOTE: How many targets it should pierce
        this.setShotFromCrossbow(true);  // NOTE: Crossbow has piercing, so let's try this


        //this.damage = ATTACK_DAMAGE;
    }

    public EntityTideTrident(World worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        this(IafEntityRegistry.TIDE_TRIDENT, worldIn);
        this.setPosition(thrower.getPosX(), thrower.getPosYEye() - (double)0.1F, thrower.getPosZ());
        this.setShooter(thrower);
        thrownStack = thrownStackIn;
        this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(thrownStackIn));
        this.dataManager.set(field_226571_aq_, thrownStackIn.hasEffect());
        this.setPierceLevel((byte)PIERCE_LEVEL);

    }


    public EntityTideTrident(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.TIDE_TRIDENT, worldIn);

    }

    public EntityTideTrident(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        thrownStack = new ItemStack(IafItemRegistry.TIDE_TRIDENT);
        this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(thrownStack));
        this.dataManager.set(field_226571_aq_, thrownStack.hasEffect());
        this.setPierceLevel((byte)PIERCE_LEVEL);  // NOTE: How many targets it should pierce
        this.setShotFromCrossbow(true);  // NOTE: Crossbow has piercing, so let's try this
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float f = ATTACK_DAMAGE;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entity;
            f += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
        }

        Entity entity1 = this.func_234616_v_();
        DamageSource damagesource = DamageSource.causeTridentDamage(this, entity1 == null ? this : entity1);

        this.dealtDamage = true;
        /*
        Same as above line
        try {
            Field dealtDamageField = ObfuscationReflectionHelper.findField(EntityTideTrident.class, "field_226571_aq_");
            Field modifier = Field.class.getDeclaredField("modifiers");
            modifier.setAccessible(true);
            dealtDamageField.set(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
        SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.attackEntityFrom(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity) entity1, livingentity1);
                }

                this.arrowHit(livingentity1);
            }
        }

        this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.thrownStack)) {
            BlockPos blockpos = entity.getPosition();
            if (this.world.canSeeSky(blockpos)) {
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.world);
                lightningboltentity.moveForced(Vector3d.copyCentered(blockpos));
                lightningboltentity.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
                this.world.addEntity(lightningboltentity);
                soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundevent, f1, 1.0F);
    }



    //@Override
    /*
    protected void old_onEntityHit(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float f = ATTACK_DAMAGE;  // NOTE: tweakbsd added damage to be in sync with corresponding item
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            f += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
        }

        Entity entity1 = this.func_234616_v_();

        DamageSource damagesource = DamageSource.causeTridentDamage(this, (Entity)(entity1 == null ? this : entity1));
        this.dealtDamage = true;  // NOTE: tweakbsd added to accesstransformers.cfg cause was missing and trident had weird behaviour after succeessful hit.


        NOTE: Same as above line without accesstransformer.cfg changes
        try {
            Field dealtDamageField = ObfuscationReflectionHelper.findField(EntityTideTrident.class, "field_226571_aq_");
            Field modifier = Field.class.getDeclaredField("modifiers");
            modifier.setAccessible(true);
            dealtDamageField.set(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.attackEntityFrom(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity1);
                }

                this.arrowHit(livingentity1);
            }
        }

        this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
        float f1 = 1.0F;
        if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.thrownStack)) {
            BlockPos blockpos = entity.func_233580_cy_();
            if (this.world.canSeeSky(blockpos)) {
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.world);
                lightningboltentity.func_233576_c_(Vector3d.func_237492_c_(blockpos));
                lightningboltentity.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
                this.world.addEntity(lightningboltentity);
                soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.playSound(soundevent, f1, 1.0F);
    }

    */

}