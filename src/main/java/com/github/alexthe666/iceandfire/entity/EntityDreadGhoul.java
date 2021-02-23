package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class EntityDreadGhoul extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {

    private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityDreadGhoul.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityDreadGhoul.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SCREAMS = EntityDataManager.createKey(EntityDreadGhoul.class, DataSerializers.VARINT);
    private static final float INITIAL_WIDTH = 0.6F;
    private static final float INITIAL_HEIGHT = 1.8F;
    public static Animation ANIMATION_SPAWN = Animation.create(40);
    public static Animation ANIMATION_SLASH = Animation.create(25);
    private int animationTick;
    private Animation currentAnimation;
    private int hostileTicks = 0;
    private float firstWidth = 1.0F;
    private float firstHeight = 1.0F;

    public EntityDreadGhoul(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.5D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, IDreadMob.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new DreadAITargetNonDread(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity entity) {
                return DragonUtils.canHostilesTarget(entity);
            }
        }));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 30.0D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 128.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(SCREAMS, Integer.valueOf(0));
        this.dataManager.register(SCALE, Float.valueOf(1F));
    }

    public float getScale() {
        return Float.valueOf(this.dataManager.get(SCALE).floatValue());
    }

    public void setScale(float scale) {
        this.dataManager.set(SCALE, Float.valueOf(scale));
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_SLASH);
        }
        return true;
    }

    public void livingTick() {
        super.livingTick();
        if (Math.abs(firstWidth - INITIAL_WIDTH * getScale()) > 0.01F || Math.abs(firstHeight - INITIAL_HEIGHT * getScale()) > 0.01F) {
            firstWidth = INITIAL_WIDTH * getScale();
            firstHeight = INITIAL_HEIGHT * getScale();
        }
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = world.getBlockState(this.getPosition().down());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, belowBlock), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getBoundingBox().minY, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
                }
            }
            this.setMotion(0, this.getMotion().y, this.getMotion().z);
        }
        if (this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < 4 && this.canEntityBeSeen(this.getAttackTarget())) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_SLASH);
            }
            this.faceEntity(this.getAttackTarget(), 360, 80);
            if (this.getAnimation() == ANIMATION_SLASH && (this.getAnimationTick() == 9 || this.getAnimationTick() == 19)) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                this.getAttackTarget().applyKnockback(0.25F, this.getPosX() - this.getAttackTarget().getPosX(), this.getPosZ() - this.getAttackTarget().getPosZ());
            }
        }
        if (!world.isRemote) {
            if (this.getAttackTarget() != null) {
                hostileTicks++;
                if (this.getScreamStage() == 0) {
                    if (hostileTicks > 20) {
                        this.setScreamStage(1);
                    }
                } else {
                    if (this.ticksExisted % 20 < 10) {
                        this.setScreamStage(1);
                    } else {
                        this.setScreamStage(2);
                    }
                }
            } else {
                if (this.getScreamStage() > 0) {
                    if (this.ticksExisted % 20 < 10 && this.getScreamStage() == 2) {
                        this.setScreamStage(1);
                    } else {
                        this.setScreamStage(0);
                    }
                }
                hostileTicks = 0;
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("ScreamStage", this.getScreamStage());
        compound.putFloat("DreadScale", this.getScale());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setScreamStage(compound.getInt("ScreamStage"));
        this.setScale(compound.getFloat("DreadScale"));
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, variant);
    }

    public int getScreamStage() {
        return this.dataManager.get(SCREAMS).intValue();
    }

    public void setScreamStage(int screamStage) {
        this.dataManager.set(SCREAMS, screamStage);
    }

    public float getRenderScale() {
        return getScale();
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAnimation(ANIMATION_SPAWN);
        this.setVariant(rand.nextInt(3));
        return data;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPAWN, ANIMATION_SLASH};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldFear() {
        return true;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.DREAD_GHOUL_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
    }

    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.70F;
    }
}