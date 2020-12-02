package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityDragonArrow extends AbstractArrowEntity {

    public static float ATTACK_DAMAGE = 10.0F;  // NOTE: tweakbsd added constant

    public EntityDragonArrow(EntityType typeIn, World worldIn) {
        super(typeIn, worldIn);
        this.setDamage(ATTACK_DAMAGE);
    }

    public EntityDragonArrow(EntityType typeIn, double x, double y, double z, World world) {
        super(typeIn, x, y, z, world);
        this.setDamage(ATTACK_DAMAGE);
    }

    public EntityDragonArrow(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.DRAGON_ARROW, worldIn);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntityDragonArrow(EntityType typeIn, LivingEntity shooter, World worldIn) {
        super(typeIn, shooter, worldIn);
        this.setDamage(ATTACK_DAMAGE);  // NOTE: tweakbsd fixed call to setDamage()
    }

    @Override
    public void writeAdditional(CompoundNBT tagCompound) {
        super.writeAdditional(tagCompound);
        tagCompound.putDouble("damage", ATTACK_DAMAGE);
    }

    @Override
    public void readAdditional(CompoundNBT tagCompund) {
        super.readAdditional(tagCompund);
        this.setDamage(tagCompund.getDouble("damage"));
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(IafItemRegistry.DRAGONBONE_ARROW);
    }

}