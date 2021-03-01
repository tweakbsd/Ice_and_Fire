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


    protected void onEntityHit_TryToFixPiercing(EntityRayTraceResult p_213868_1_) {
        //super.onEntityHit(p_213868_1_);
        // NOTE: Cannot call super, or we get normal Trident behaviour and no piercing

        Entity entity = p_213868_1_.getEntity();
        float f =  ATTACK_DAMAGE;  //(float)this.getMotion().length();
        int i = MathHelper.ceil(MathHelper.clamp((double)f, 0.0D, 2.147483647E9D));
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }

            if (this.hitEntities == null) {
                this.hitEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {

                System.out.println("PIERCED more than 2 entities. Setting this.dealtDamage to TRUE and doing nothing...");

                // NOTE: tweakbsd added code, last pierced entity, need to reset hitEntities / piercedEntities
                this.hitEntities = null;
                this.piercedEntities = null;

                this.dealtDamage = true;
                return;
            }

            System.out.println("PIERCED adding Entity ID to list ...");
            this.piercedEntities.add(entity.getEntityId());
        }

        if (this.getIsCritical()) {
            long j = (long)this.rand.nextInt(i / 2 + 2);
            i = (int)Math.min(j + (long)i, 2147483647L);
        }

        SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;

        Entity entity1 = this.func_234616_v_();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = DamageSource.causeTridentDamage(this, this);
        } else {
            damagesource = DamageSource.causeTridentDamage(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity)entity1).setLastAttackedEntity(entity);
            }
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        int k = entity.getFireTimer();
        if (this.isBurning() && !flag) {
            entity.setFire(5);
        }

        if (entity.attackEntityFrom(damagesource, (float)i)) {
            if (flag) {
                return;
            }
            
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;

                /*
                if (!this.world.isRemote && this.getPierceLevel() <= 0) {

                    // NOTE: No piercing, we are done
                    //livingentity.setArrowCountInEntity(livingentity.getArrowCountInEntity() + 1);
                }
                */

                if (this.knockbackStrength > 0) {
                    Vector3d vector3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockbackStrength * 0.6D);
                    if (vector3d.lengthSquared() > 0.0D) {
                        livingentity.addVelocity(vector3d.x, 0.1D, vector3d.z);
                    }
                }

                if (!this.world.isRemote && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity);
                }

                System.out.println("PIERCED attackEntity() was TRUE, calling arrowHit() ...");
                this.arrowHit(livingentity);

                if (entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)entity1).connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241770_g_, 0.0F));
                }

                if (!entity.isAlive() && this.hitEntities != null) {

                    System.out.println("PIERCED adding livingentity to hitEntities list... size before " + this.hitEntities.size());

                    this.hitEntities.add(livingentity);
                }


                this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
                float f1 = 1.0F;

                System.out.println("PIERCED checking for thunder and channeling...");
                if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.thrownStack)) {
                    BlockPos blockpos = entity.func_233580_cy_();

                    if (this.world.canSeeSky(blockpos)) {
                        LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.world);
                        lightningboltentity.func_233576_c_(Vector3d.func_237492_c_(blockpos));
                        lightningboltentity.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
                        this.world.addEntity(lightningboltentity);
                        soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
                        //f1 = 5.0F;
                    }
                }

            }

            this.playSound(soundevent, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

        } else {

            System.out.println("PIERCED attackEntity() was FALSE.");

            // NOTE: this.fire = k
            entity.func_241209_g_(k);
            this.setMotion(this.getMotion().scale(-0.1D));
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;

            /*
            if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
                if (this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1F);
                }
                this.remove();
            }
             */
        }


        //this.playSound(soundevent, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));


    }

    //@Override
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

        /*
        NOTE: Same as above line without accesstransformer.cfg changes
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

}