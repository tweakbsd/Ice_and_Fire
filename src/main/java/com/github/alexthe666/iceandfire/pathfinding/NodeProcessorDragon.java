package com.github.alexthe666.iceandfire.pathfinding;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.world.Region;

public class NodeProcessorDragon extends WalkNodeProcessor {

    public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
        super.func_225578_a_(p_225578_1_, p_225578_2_);
        //this.entitySizeX = 1;

        /*if(p_225578_2_ instanceof EntityDragonBase) {
            EntityDragonBase dragon = (EntityDragonBase)p_225578_2_;

            float dragonHeight = dragon.getHeight();
            System.out.println("NodeProcessorDragon() dragonHeight: " + dragonHeight);

        }*/
        // NOTE: tweakbsd Let's try this, maybe this helps Dragons canWalk Down slabs
        this.entitySizeY = 2;

        this.entitySizeZ = 1;
    }
}
