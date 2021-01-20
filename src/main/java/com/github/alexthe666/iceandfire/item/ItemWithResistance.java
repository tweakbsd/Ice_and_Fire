package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ItemEntityWithResistance;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;



public class ItemWithResistance extends ItemGeneric {

    protected boolean isImmuneToExplosions = false;
    protected boolean isImmuneToFire = false;
    protected boolean isImmuneToLightning = false;
    protected boolean isImmuneToCactus = false;

    public ItemWithResistance(String name) {
        super(name, new Item.Properties().group(IceAndFire.TAB_ITEMS));
    }

    public ItemWithResistance makeImmuneToFire() {
        this.isImmuneToFire = true;
        return this;
    }

    public ItemWithResistance makeImmuneToLightning() {
        this.isImmuneToLightning = true;
        return this;
    }

    public ItemWithResistance makeImmuneToExplosions() {
        this.isImmuneToExplosions = true;
        return this;
    }

    public ItemWithResistance makeImmuneToCactus() {
        this.isImmuneToCactus = true;
        return this;
    }

    public boolean isImmuneToExplosions() {
        return this.isImmuneToExplosions;
    }

    public void setIsImmuneToExplosions(boolean value) {
        this.isImmuneToExplosions = value;
    }

    public boolean isImmuneToFire() {
        return this.isImmuneToFire;
    }

    public void setIsImmuneToFire(boolean value) {
        this.isImmuneToFire = value;
    }

    public boolean isImmuneToLightning() {
        return this.isImmuneToLightning;
    }

    public void setIsImmuneToLightning(boolean value) {
        this.isImmuneToLightning = value;
    }

    public boolean isImmuneToCactus() {
        return this.isImmuneToCactus;
    }

    public void setIsImmuneToCactus(boolean value) {
        this.isImmuneToCactus = value;
    }

    public boolean hasCustomEntity(ItemStack stack) {
        if(stack != null && !stack.isEmpty() && (stack.getItem() instanceof ItemWithResistance)) {
            System.out.println("IItemWithResistance.hasCustomEntity() returning true");
            return true;
        }
        System.out.println("IItemWithResistance.hasCustomEntity() returning false");
        return false;
    }

    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        if (location.getClass().equals(ItemEntity.class)) {

            ItemEntityWithResistance newEntity = new ItemEntityWithResistance( (ItemEntity)location);
            newEntity.setIsImmuneToExplosions(this.isImmuneToExplosions);
            newEntity.setIsImmuneToFire(this.isImmuneToFire);
            newEntity.setIsImmuneToLightning(this.isImmuneToLightning);
            newEntity.setIsImmuneToCactus(this.isImmuneToCactus);

            System.out.println("ItemWithResistance.createEntity() returning new ItemEntityWithResistance");

            return newEntity;
        }
        System.out.println("ItemWithResistance.createEntity() returning NULL");
        return null;
    }

}
