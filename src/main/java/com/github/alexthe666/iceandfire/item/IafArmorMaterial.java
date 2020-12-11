package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.UUID;

public class IafArmorMaterial extends CustomArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private int maxDamageFactor;

    public IafArmorMaterial(String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResistance) {
        super(name, durability, damageReduction, encantability, sound, toughness, knockbackResistance);
        this.maxDamageFactor = durability;
    }

    public int getDurability(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    public float func_230304_f_(){
        return this.knockbackResistance;
    }

}
