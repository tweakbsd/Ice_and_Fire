package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.client.model.util.HideableModelRenderer;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class ModelDreadQueen extends ModelDragonBase<EntityDreadQueen> {
    public HideableModelRenderer body;
    public HideableModelRenderer chestplate;
    public HideableModelRenderer head;
    public HideableModelRenderer armRight;
    public HideableModelRenderer legRight;
    public HideableModelRenderer armLeft;
    public HideableModelRenderer legLeft;
    public HideableModelRenderer cloak;
    public HideableModelRenderer necklace;
    public HideableModelRenderer crown;
    public HideableModelRenderer sleeveRight;
    public HideableModelRenderer robeLowerRight;
    public HideableModelRenderer sleeveLeft;
    public HideableModelRenderer robeLowerLeft;
    public BipedModel.ArmPose leftArmPose;
    public BipedModel.ArmPose rightArmPose;
    public boolean isSneak;
    private ModelAnimator animator;
    private boolean armor = false;

    public ModelDreadQueen(float modelSize, boolean armorArms) {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.armor = armorArms;
        this.leftArmPose = BipedModel.ArmPose.EMPTY;
        this.rightArmPose = BipedModel.ArmPose.EMPTY;
        this.body = new HideableModelRenderer(this, 16, 16);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        this.crown = new HideableModelRenderer(this, 58, 0);
        this.crown.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.crown.addBox(-4.5F, -12.5F, -4.5F, 9, 13, 9, 0.0F);
        this.legRight = new HideableModelRenderer(this, 0, 16);
        this.legRight.setRotationPoint(-1.9F, 12.0F, 0.1F);
        this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.sleeveLeft = new HideableModelRenderer(this, 36, 33);
        this.sleeveLeft.mirror = true;
        this.sleeveLeft.setRotationPoint(-0.5F, -0.1F, 0.0F);
        this.sleeveLeft.addBox(-0.5F, -2.1F, -2.4F, 5, 6, 5, 0.0F);
        this.robeLowerLeft = new HideableModelRenderer(this, 58, 33);
        this.robeLowerLeft.setRotationPoint(0.0F, -0.2F, 0.0F);
        this.robeLowerLeft.addBox(-1.9F, 0.0F, -2.5F, 4, 7, 5, 0.0F);
        this.chestplate = new HideableModelRenderer(this, 1, 32);
        this.chestplate.setRotationPoint(0.0F, 0.1F, 0.0F);
        this.chestplate.addBox(-4.5F, -0.3F, -2.5F, 9, 12, 5, 0.0F);
        this.robeLowerRight = new HideableModelRenderer(this, 58, 33);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setRotationPoint(0.0F, -0.2F, 0.0F);
        this.robeLowerRight.addBox(-2.1F, 0.0F, -2.5F, 4, 7, 5, 0.0F);
        this.sleeveRight = new HideableModelRenderer(this, 36, 33);
        this.sleeveRight.setRotationPoint(0.5F, -0.1F, 0.0F);
        this.sleeveRight.addBox(-4.5F, -2.1F, -2.4F, 5, 6, 5, 0.0F);
        this.cloak = new HideableModelRenderer(this, 81, 37);
        this.cloak.setRotationPoint(0.0F, 0.1F, 0.0F);
        this.cloak.addBox(-4.5F, 0.0F, 2.3F, 9, 21, 1, 0.0F);
        this.setRotateAngle(cloak, 0.045553093477052F, 0.0F, 0.0F);
        this.armRight = new HideableModelRenderer(this, 40, 16);
        this.armRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.armRight.addBox(-2.5F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(armRight, 0.0F, 0.0F, 0.10000736613927509F);
        this.necklace = new HideableModelRenderer(this, 1, 52);
        this.necklace.setRotationPoint(0.0F, 0.1F, 0.0F);
        this.necklace.addBox(-4.5F, -0.3F, -2.8F, 9, 7, 5, 0.0F);
        this.setRotateAngle(necklace, 0.032114058236695664F, 0.0F, 0.0F);
        this.armLeft = new HideableModelRenderer(this, 40, 16);
        this.armLeft.mirror = true;
        this.armLeft.setRotationPoint(5.0F, 2.0F, -0.0F);
        this.armLeft.addBox(-0.5F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(armLeft, -0.0F, 0.0F, -0.10000736613927509F);
        this.head = new HideableModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.legLeft = new HideableModelRenderer(this, 0, 16);
        this.legLeft.mirror = true;
        this.legLeft.setRotationPoint(1.9F, 12.0F, 0.1F);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        this.head.addChild(this.crown);
        this.body.addChild(this.legRight);
        this.armLeft.addChild(this.sleeveLeft);
        this.legLeft.addChild(this.robeLowerLeft);
        this.body.addChild(this.chestplate);
        this.legRight.addChild(this.robeLowerRight);
        this.armRight.addChild(this.sleeveRight);
        this.body.addChild(this.cloak);
        this.body.addChild(this.armRight);
        this.chestplate.addChild(this.necklace);
        this.body.addChild(this.armLeft);
        this.body.addChild(this.head);
        this.body.addChild(this.legLeft);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void setLivingAnimations(EntityDreadQueen LivingEntityIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        this.rightArmPose = BipedModel.ArmPose.EMPTY;
        this.leftArmPose = BipedModel.ArmPose.EMPTY;
        ItemStack itemstack = LivingEntityIn.getHeldItem(Hand.MAIN_HAND);
        super.setLivingAnimations(LivingEntityIn, limbSwing, limbSwingAmount, partialTickTime);
    }

    public void setRotationAngles(EntityDreadQueen entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1);
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        this.faceTarget(netHeadYaw, headPitch, 1.0F, head);
        float f = 1.0F;
        this.armRight.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.armLeft.rotateAngleX += MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
        this.legRight.rotateAngleY = 0.0F;
        this.legLeft.rotateAngleY = 0.0F;
        this.legRight.rotateAngleZ = 0.0F;
        this.legLeft.rotateAngleZ = 0.0F;

        if (entityIn.isPassenger()) {
            this.armRight.rotateAngleX += -((float) Math.PI / 5F);
            this.armLeft.rotateAngleX += -((float) Math.PI / 5F);
            this.legRight.rotateAngleX = -1.4137167F;
            this.legRight.rotateAngleY = ((float) Math.PI / 10F);
            this.legRight.rotateAngleZ = 0.07853982F;
            this.legLeft.rotateAngleX = -1.4137167F;
            this.legLeft.rotateAngleY = -((float) Math.PI / 10F);
            this.legLeft.rotateAngleZ = -0.07853982F;
        }
        if (this.swingProgress > 0.0F) {
            HandSide Handside = this.getMainHand(entityIn);
            ModelRenderer modelrenderer = this.getArmForSide(Handside);
            float f1 = this.swingProgress;
            this.body.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (Handside == HandSide.LEFT) {
                this.body.rotateAngleY *= -1.0F;
            }

            this.armRight.rotationPointZ = MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armRight.rotationPointX = -MathHelper.cos(this.body.rotateAngleY) * 5.0F;
            this.armLeft.rotationPointZ = -MathHelper.sin(this.body.rotateAngleY) * 5.0F;
            this.armLeft.rotationPointX = MathHelper.cos(this.body.rotateAngleY) * 5.0F;
            this.armRight.rotateAngleY += this.body.rotateAngleY;
            this.armLeft.rotateAngleY += this.body.rotateAngleY;
            this.armLeft.rotateAngleX += this.body.rotateAngleY;
            f1 = 1.0F - this.swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
            modelrenderer.rotateAngleY += this.body.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
        }
        if (this.isSneak) {
            this.body.rotateAngleX = 0.5F;
            this.armRight.rotateAngleX += 0.4F;
            this.armLeft.rotateAngleX += 0.4F;
            this.legRight.rotationPointZ = 4.0F;
            this.legLeft.rotationPointZ = 4.0F;
            this.legRight.rotationPointY = 9.0F;
            this.legLeft.rotationPointY = 9.0F;
            this.head.rotationPointY = 1.0F;
        } else {
            this.body.rotateAngleX = 0.0F;
            this.legRight.rotationPointZ = 0.1F;
            this.legLeft.rotationPointZ = 0.1F;
            this.legRight.rotationPointY = 12.0F;
            this.legLeft.rotationPointY = 12.0F;
            this.head.rotationPointY = 0.0F;
        }

        this.armRight.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armLeft.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.armRight.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.armLeft.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        if (entityIn.getAnimation() == EntityDreadQueen.ANIMATION_SPAWN) {
            //this.walk(armRight, 1.5F, 0.4F, false, 2, -0.3F, thrall.ticksExisted, 1);
            //this.walk(armLeft, 1.5F,  0.4F, true, 2, 0.3F, thrall.ticksExisted, 1);
            if (entityIn.getAnimationTick() < 30) {
                this.flap(armRight, 0.5F, 0.5F, false, 2, -0.7F, entityIn.ticksExisted, 1);
                this.flap(armLeft, 0.5F, 0.5F, true, 2, -0.7F, entityIn.ticksExisted, 1);
                this.walk(armRight, 0.5F, 0.5F, true, 1, 0, entityIn.ticksExisted, 1);
                this.walk(armLeft, 0.5F, 0.5F, true, 1, 0, entityIn.ticksExisted, 1);
            }
        }

    }


    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        animator.update(entity);
        animator.setAnimation(EntityDreadQueen.ANIMATION_SPAWN);
        animator.startKeyframe(0);
        animator.move(this.body, 0, 35, 0);
        rotate(animator, this.armLeft, -180, 0, 0);
        rotate(animator, this.armRight, -180, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(30);
        animator.move(this.body, 0, 0, 0);
        rotate(animator, this.armLeft, -180, 0, 0);
        rotate(animator, this.armRight, -180, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    protected ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.armLeft : this.armRight;
    }

    protected HandSide getMainHand(Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            LivingEntity LivingEntity = (LivingEntity) entityIn;
            HandSide Handside = LivingEntity.getPrimaryHand();
            return LivingEntity.swingingHand == Hand.MAIN_HAND ? Handside : Handside.opposite();
        } else {
            return HandSide.RIGHT;
        }
    }

    public void setVisible(boolean visible) {
        this.head.invisible = !visible;
        this.body.invisible = !visible;
        this.armRight.invisible = !visible;
        this.armLeft.invisible = !visible;
        this.legRight.invisible = !visible;
        this.legLeft.invisible = !visible;
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }
}
