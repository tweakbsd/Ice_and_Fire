package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.entity.EntityPropertiesHandler;
import com.github.alexthe666.citadel.server.item.CustomToolMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.ItemEntityWithResistance;
import com.github.alexthe666.iceandfire.entity.props.FrozenEntityProperties;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModSwordWithResistance extends ItemModSword {

    protected boolean isImmuneToExplosions = false;
    protected boolean isImmuneToFire = false;
    protected boolean isImmuneToLightning = false;

    public ItemModSwordWithResistance(CustomToolMaterial toolmaterial, String gameName, Properties properties) {
        super(toolmaterial, gameName, properties.group(IceAndFire.TAB_ITEMS));
    }

    public ItemModSwordWithResistance(CustomToolMaterial toolmaterial, String gameName) {
        super(toolmaterial, gameName, new Properties().group(IceAndFire.TAB_ITEMS));
    }

    public ItemModSwordWithResistance makeImmuneToFire() {
        this.isImmuneToFire = true;
        return this;
    }

    public ItemModSwordWithResistance makeImmuneToLightning() {
        this.isImmuneToLightning = true;
        return this;
    }

    public ItemModSwordWithResistance makeImmuneToExplosions() {
        this.isImmuneToExplosions = true;
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

    public boolean hasCustomEntity(ItemStack stack) {
        if(stack != null && !stack.isEmpty() && (stack.getItem() instanceof ItemModSword)) {
            System.out.println("IItemWithResistance.hasCustomEntity() returning true");
            return true;
        }
        System.out.println("IItemWithResistance.hasCustomEntity() returning false");
        return false;
    }

    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        if (location.getClass().equals(ItemEntity.class)) {

            ItemEntityWithResistance newEntity = new ItemEntityWithResistance( (ItemEntity) location);
            newEntity.setIsImmuneToExplosions(this.isImmuneToExplosions);
            newEntity.setIsImmuneToFire(this.isImmuneToFire);
            newEntity.setIsImmuneToLightning(this.isImmuneToLightning);

            System.out.println("ItemWithResistance.createEntity() returning new ItemEntityWithResistance");

            return newEntity;
        }
        System.out.println("ItemWithResistance.createEntity() returning NULL");
        return null;
    }

}
