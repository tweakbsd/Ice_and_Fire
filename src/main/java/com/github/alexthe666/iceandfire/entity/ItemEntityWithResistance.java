package com.github.alexthe666.iceandfire.entity;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Collections;
import java.util.List;

public class ItemEntityWithResistance extends ItemEntity {

    private static final DataParameter<Boolean> IMMUNE_TO_EXPLOSIONS = EntityDataManager.createKey(ItemEntityWithResistance.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IMMUNE_TO_FIRE = EntityDataManager.createKey(ItemEntityWithResistance.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IMMUNE_TO_LIGHTNING = EntityDataManager.createKey(ItemEntityWithResistance.class, DataSerializers.BOOLEAN);

    public boolean isImmuneToExplosions = false;
    public boolean isImmuneToFire = false;
    public boolean isImmuneToLightning = false;
    //private List<DamageSource> immuneToDamageSources = Lists.newArrayList();

    public ItemEntityWithResistance(EntityType entityType, World world) {
        super(entityType, world);
    }

    public ItemEntityWithResistance(World worldIn, double x, double y, double z) {
        this(IafEntityRegistry.ITEM_ENTITY_WITH_RESISTANCE, worldIn);
        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    }

    public ItemEntityWithResistance(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
        this.lifespan = (stack.getItem() == null) ? 6000 : stack.getEntityLifespan(worldIn);
    }

    public ItemEntityWithResistance(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.ITEM_ENTITY_WITH_RESISTANCE, worldIn);
    }

    @Override
    protected void registerData() {

        // TODO: Add fields for damagesources
        super.registerData();

        this.getDataManager().register(IMMUNE_TO_EXPLOSIONS, this.isImmuneToExplosions);
        this.getDataManager().register(IMMUNE_TO_FIRE, this.isImmuneToFire);
        this.getDataManager().register(IMMUNE_TO_LIGHTNING, this.isImmuneToLightning);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        this.setIsImmuneToLightning(compound.getBoolean("ImmuneToLightning"));
        this.setIsImmuneToFire(compound.getBoolean("ImmuneToFire"));
        this.setIsImmuneToExplosions(compound.getBoolean("ImmuneToExplosions"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        compound.putBoolean("ImmuneToLightning", this.isImmuneToLightning);
        compound.putBoolean("ImmuneToFire", this.isImmuneToFire);
        compound.putBoolean("ImmuneToExplosions", this.isImmuneToExplosions);
    }

    ItemEntityWithResistance makeImmuneToFire() {
        //this.immuneToDamageSources.add(DamageSource.IN_FIRE);
        //this.immuneToDamageSources.add(DamageSource.ON_FIRE);
        this.setIsImmuneToFire(true);
        return this;
    }

    ItemEntityWithResistance makeImmuneToLightning() {
        this.setIsImmuneToLightning(true);
        return this;
    }

    ItemEntityWithResistance makeImmuneToExplosions() {
        this.setIsImmuneToExplosions(true);
        return this;
    }


    //ItemEntityWithResistance makeImmuneTo(DamageSource source) {
    //    this.immuneToDamageSources.add(source);
    //    return this;
    //}

    //@OnlyIn(Dist.CLIENT)
    public ItemEntityWithResistance(ItemEntity itemEntityIn) {
        this(IafEntityRegistry.ITEM_ENTITY_WITH_RESISTANCE, itemEntityIn.world);
        this.setItem(itemEntityIn.getItem().copy());
        this.setMotion(itemEntityIn.getMotion());
        this.setPickupDelay(40);
        this.setThrowerId(itemEntityIn.getThrowerId());
        this.copyLocationAndAnglesFrom(itemEntityIn);
        // accesstranformer necessary
        //this.hoverStart = p_i231561_1_.hoverStart;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    public boolean isInvulnerableTo(DamageSource source) {

        // NOTE: tweakbsd I need to get used to java list iterators...this look ugly
        /*
        DamageSource[] damageSourceArray = (DamageSource[])this.immuneToDamageSources.toArray();
        for(int i = 0; i < damageSourceArray.length; i++) {
            if(damageSourceArray[i] == source) return true;
        }
        */
        if(this.isImmuneToLightning() && source == DamageSource.LIGHTNING_BOLT) {
            System.out.println("Item isInvulnerableTo(lightning) true");

            return true;
        }
        else if(this.isImmuneToExplosions() && source.isExplosion()) {
            System.out.println("Item isInvulnerableTo(explosion) true");
            return true;
        } else if(this.isImmuneToFire() && (source.isFireDamage() || source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE)) {
            System.out.println("Item isInvulnerableTo(fire) true");
            return true;
        }

        System.out.println("Item isInvulnerableTo(" + source.toString() + ") calling super");
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        System.out.println("Item attackEntityFrom() -> " + source.toString());
        return super.attackEntityFrom(source, amount);
    }

    public boolean isImmuneToFire() {
        this.isImmuneToFire = this.getDataManager().get(IMMUNE_TO_FIRE).booleanValue();
        return this.isImmuneToFire;
    }

    public void setIsImmuneToFire(boolean immune) {
        this.isImmuneToFire = immune;
        this.getDataManager().set(IMMUNE_TO_FIRE, immune);
    }

    public boolean isImmuneToLightning() {
        this.isImmuneToLightning = this.getDataManager().get(IMMUNE_TO_LIGHTNING).booleanValue();
        return this.isImmuneToLightning;
    }

    public void setIsImmuneToLightning(boolean immune) {
        this.isImmuneToLightning = immune;
        this.getDataManager().set(IMMUNE_TO_LIGHTNING, immune);
    }

    @Override
    public boolean isImmuneToExplosions() {
        this.isImmuneToExplosions = this.getDataManager().get(IMMUNE_TO_EXPLOSIONS).booleanValue();
        return this.isImmuneToExplosions;
        //return ;
    }

    public void setIsImmuneToExplosions(boolean immune) {
        this.isImmuneToExplosions = immune;
        this.getDataManager().set(IMMUNE_TO_EXPLOSIONS, immune);
    }

    // NOTE: Vanilla isImmuneToFire()
    public boolean func_230279_az_() {
        return this.isImmuneToFire;
        //return super.func_230279_az_();
    }
}
