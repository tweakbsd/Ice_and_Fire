package com.github.alexthe666.iceandfire.client.model.animator;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ITabulaModelAnimator;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonAnimations;
import com.github.alexthe666.iceandfire.client.model.util.LegArticulator;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceDragonTabulaModelAnimator extends IceAndFireTabulaModelAnimator implements ITabulaModelAnimator<EntityIceDragon> {

    private TabulaModel[] walkPoses = {EnumDragonAnimations.WALK1.icedragon_model, EnumDragonAnimations.WALK2.icedragon_model, EnumDragonAnimations.WALK3.icedragon_model, EnumDragonAnimations.WALK4.icedragon_model};
    private TabulaModel[] flyPoses = {EnumDragonAnimations.FLIGHT1.icedragon_model, EnumDragonAnimations.FLIGHT2.icedragon_model, EnumDragonAnimations.FLIGHT3.icedragon_model, EnumDragonAnimations.FLIGHT4.icedragon_model, EnumDragonAnimations.FLIGHT5.icedragon_model, EnumDragonAnimations.FLIGHT6.icedragon_model};
    private TabulaModel[] swimPoses = {EnumDragonAnimations.SWIM1.icedragon_model, EnumDragonAnimations.SWIM2.icedragon_model, EnumDragonAnimations.SWIM3.icedragon_model, EnumDragonAnimations.SWIM4.icedragon_model, EnumDragonAnimations.SWIM5.icedragon_model};
    private AdvancedModelBox[] neckParts;
    private AdvancedModelBox[] tailParts;
    private AdvancedModelBox[] tailPartsWBody;
    private AdvancedModelBox[] toesPartsL;
    private AdvancedModelBox[] toesPartsR;
    private AdvancedModelBox[] clawL;
    private AdvancedModelBox[] clawR;

    public IceDragonTabulaModelAnimator() {
        super(EnumDragonAnimations.GROUND_POSE.icedragon_model);
    }

    public void init(TabulaModel model) {
        neckParts = new AdvancedModelBox[]{model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Neck3"), model.getCube("Head")};
        tailParts = new AdvancedModelBox[]{model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
        tailPartsWBody = new AdvancedModelBox[]{model.getCube("BodyLower"), model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4")};
        toesPartsL = new AdvancedModelBox[]{model.getCube("ToeL1"), model.getCube("ToeL2"), model.getCube("ToeL3")};
        toesPartsR = new AdvancedModelBox[]{model.getCube("ToeR1"), model.getCube("ToeR2"), model.getCube("ToeR3")};
        clawL = new AdvancedModelBox[]{model.getCube("ClawL")};
        clawR = new AdvancedModelBox[]{model.getCube("ClawR")};
    }

    @Override
    public void setRotationAngles(TabulaModel model, EntityIceDragon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.resetToDefaultPose();
        init(model);
        animate(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);
        boolean walking = !entity.isHovering() && !entity.isFlying() && entity.hoverProgress <= 0 && entity.flyProgress <= 0;
        boolean swimming = entity.isInWater() && entity.swimProgress > 0;
        int currentIndex = walking ? (entity.walkCycle / 10) : (entity.flightCycle / 10);
        if (swimming) {
            currentIndex = entity.swimCycle / 10;
        }
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            prevIndex = swimming ? 4 : walking ? 3 : 5;
        }
        TabulaModel currentPosition = swimming ? swimPoses[currentIndex] : walking ? walkPoses[currentIndex] : flyPoses[currentIndex];
        TabulaModel prevPosition = swimming ? swimPoses[prevIndex] : walking ? walkPoses[prevIndex] : flyPoses[prevIndex];
        float delta = ((walking ? entity.walkCycle : entity.flightCycle) / 10.0F) % 1.0F;
        if (swimming) {
            delta = ((entity.swimCycle) / 10.0F) % 1.0F;
        }
        float deltaTicks = delta + (Minecraft.getInstance().getRenderPartialTicks() / 10.0F);
        if (delta == 0) {
            deltaTicks = 0;
        }


        for (AdvancedModelBox cube : model.getCubes().values()) {
            this.genderMob(entity, cube);
            if (!swimming && walking && entity.flyProgress <= 0.0F && entity.hoverProgress <= 0.0F && entity.modelDeadProgress <= 0.0F) {
                AdvancedModelBox walkPart = EnumDragonAnimations.GROUND_POSE.icedragon_model.getCube(cube.boxName);
                if (prevPosition.getCube(cube.boxName) != null) {
                    float prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
                    float prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
                    float prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
                    float x = currentPosition.getCube(cube.boxName).rotateAngleX;
                    float y = currentPosition.getCube(cube.boxName).rotateAngleY;
                    float z = currentPosition.getCube(cube.boxName).rotateAngleZ;
                    if (isHorn(cube) || isWing(model, cube) && (entity.getAnimation() == EntityDragonBase.ANIMATION_WINGBLAST || entity.getAnimation() == EntityDragonBase.ANIMATION_EPIC_ROAR)) {
                        this.addToRotateAngle(cube, limbSwingAmount, walkPart.rotateAngleX, walkPart.rotateAngleY, walkPart.rotateAngleZ);
                    } else {
                        this.addToRotateAngle(cube, limbSwingAmount, prevX + deltaTicks * distance(prevX, x), prevY + deltaTicks * distance(prevY, y), prevZ + deltaTicks * distance(prevZ, z));
                    }
                }
            }
            if (entity.modelDeadProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.DEAD.icedragon_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumDragonAnimations.DEAD.icedragon_model.getCube(cube.boxName), entity.modelDeadProgress, 20, cube.boxName.equals("ThighR") || cube.boxName.equals("ThighL"));
                }
                if (cube.boxName.equals("BodyUpper")) {
                    cube.rotationPointY += 0.35F * entity.modelDeadProgress;
                }
            }
            if (entity.sleepProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.SLEEPING_POSE.icedragon_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumDragonAnimations.SLEEPING_POSE.icedragon_model.getCube(cube.boxName), entity.sleepProgress, 20, false);
                }
            }
            if (entity.hoverProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.HOVERING_POSE.icedragon_model.getCube(cube.boxName)) && !isWing(model, cube) && !cube.boxName.contains("Tail") ) {
                    transitionTo(cube, EnumDragonAnimations.HOVERING_POSE.icedragon_model.getCube(cube.boxName), entity.hoverProgress, 20, false);
                }
            }
            if (entity.flyProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName), entity.flyProgress - entity.diveProgress * 2, 20, false);
                }
            }
            if (entity.sitProgress > 0.0F) {
                if (!entity.isPassenger()) {
                    if (!isPartEqual(cube, EnumDragonAnimations.SITTING_POSE.icedragon_model.getCube(cube.boxName))) {
                        transitionTo(cube, EnumDragonAnimations.SITTING_POSE.icedragon_model.getCube(cube.boxName), entity.sitProgress, 20, false);
                    }
                }
            }
            if (entity.ridingProgress > 0.0F) {
                if (!isHorn(cube) && EnumDragonAnimations.SIT_ON_PLAYER_POSE.icedragon_model.getCube(cube.boxName) != null && !isPartEqual(cube, EnumDragonAnimations.SIT_ON_PLAYER_POSE.icedragon_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumDragonAnimations.SIT_ON_PLAYER_POSE.icedragon_model.getCube(cube.boxName), entity.ridingProgress, 20, false);
                    if (cube.boxName.equals("BodyUpper")) {
                        cube.rotationPointZ += ((-12F - cube.rotationPointZ) / 20) * entity.ridingProgress;
                    }

                }
            }
            if (entity.tackleProgress > 0.0F) {
                if (!isPartEqual(EnumDragonAnimations.TACKLE.icedragon_model.getCube(cube.boxName), EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName)) && !isWing(model, cube)) {
                    transitionTo(cube, EnumDragonAnimations.TACKLE.icedragon_model.getCube(cube.boxName), entity.tackleProgress, 5, false);
                }
            }
            if (entity.diveProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.DIVING_POSE.icedragon_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumDragonAnimations.DIVING_POSE.icedragon_model.getCube(cube.boxName), entity.diveProgress, 10, false);
                }
            }
            if (entity.swimProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.SWIM_POSE.icedragon_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumDragonAnimations.SWIM_POSE.icedragon_model.getCube(cube.boxName), entity.swimProgress, 20, false);
                }
            }
            if (entity.fireBreathProgress > 0.0F) {
                if (!isPartEqual(cube, EnumDragonAnimations.STREAM_BREATH.icedragon_model.getCube(cube.boxName)) && !isWing(model, cube) && !cube.boxName.contains("Finger")) {
                    if (entity.prevFireBreathProgress <= entity.fireBreathProgress) {
                        transitionTo(cube, EnumDragonAnimations.BLAST_CHARGE3.icedragon_model.getCube(cube.boxName), MathHelper.clamp(entity.fireBreathProgress, 0, 5), 5, false);
                    }
                    transitionTo(cube, EnumDragonAnimations.STREAM_BREATH.icedragon_model.getCube(cube.boxName), MathHelper.clamp(entity.fireBreathProgress - 5, 0, 5), 5, false);

                }
            }
            if (!walking && !swimming) {
                AdvancedModelBox flightPart = EnumDragonAnimations.FLYING_POSE.icedragon_model.getCube(cube.boxName);
                float prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
                float prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
                float prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
                float x = currentPosition.getCube(cube.boxName).rotateAngleX;
                float y = currentPosition.getCube(cube.boxName).rotateAngleY;
                float z = currentPosition.getCube(cube.boxName).rotateAngleZ;
                if (x != flightPart.rotateAngleX || y != flightPart.rotateAngleY || z != flightPart.rotateAngleZ) {
                    this.setRotateAngle(cube, 1F, prevX + deltaTicks * distance(prevX, x), prevY + deltaTicks * distance(prevY, y), prevZ + deltaTicks * distance(prevZ, z));
                }
            }
            if (swimming && entity.flyProgress <= 0.0F && entity.hoverProgress <= 0.0F && entity.modelDeadProgress <= 0.0F) {
                AdvancedModelBox walkPart = EnumDragonAnimations.SWIM_POSE.icedragon_model.getCube(cube.boxName);
                if (prevPosition.getCube(cube.boxName) != null) {
                    float prevX = prevPosition.getCube(cube.boxName).rotateAngleX;
                    float prevY = prevPosition.getCube(cube.boxName).rotateAngleY;
                    float prevZ = prevPosition.getCube(cube.boxName).rotateAngleZ;
                    float x = currentPosition.getCube(cube.boxName).rotateAngleX;
                    float y = currentPosition.getCube(cube.boxName).rotateAngleY;
                    float z = currentPosition.getCube(cube.boxName).rotateAngleZ;
                    if (x != walkPart.rotateAngleX || y != walkPart.rotateAngleY || z != walkPart.rotateAngleZ) {
                        this.setRotateAngle(cube, limbSwingAmount, prevX + deltaTicks * distance(prevX, x), prevY + deltaTicks * distance(prevY, y), prevZ + deltaTicks * distance(prevZ, z));
                    }
                }
            }
        }
        float speed_walk = 0.2F;
        float speed_idle = entity.isSleeping() ? 0.025F : 0.05F;
        float speed_fly = 0.2F;
        float degree_walk = 0.5F;
        float degree_idle = entity.isSleeping() ? 0.25F : 0.5F;
        float degree_fly = 0.5F;
        if (!entity.isAIDisabled()) {
            if (!walking) {
                model.bob(model.getCube("BodyUpper"), -speed_fly, degree_fly * 5, false, ageInTicks, 1);
                model.walk(model.getCube("BodyUpper"), -speed_fly, degree_fly * 0.1F, false, 0, 0, ageInTicks, 1);
                model.chainWave(tailPartsWBody, speed_fly, degree_fly * -0.1F, 0, ageInTicks, 1);
                model.chainWave(neckParts, speed_fly, degree_fly * 0.2F, -4, ageInTicks, 1);
                model.chainWave(toesPartsL, speed_fly, degree_fly * 0.2F, -2, ageInTicks, 1);
                model.chainWave(toesPartsR, speed_fly, degree_fly * 0.2F, -2, ageInTicks, 1);
                model.walk(model.getCube("ThighR"), -speed_fly, degree_fly * 0.1F, false, 0, 0, ageInTicks, 1);
                model.walk(model.getCube("ThighL"), -speed_fly, degree_fly * 0.1F, true, 0, 0, ageInTicks, 1);
            } else {
                model.bob(model.getCube("BodyUpper"), speed_walk * 2, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
                model.bob(model.getCube("ThighR"), speed_walk, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
                model.bob(model.getCube("ThighL"), speed_walk, degree_walk * 1.7F, false, limbSwing, limbSwingAmount);
                model.chainSwing(tailParts, speed_walk, degree_walk * 0.25F, -2, limbSwing, limbSwingAmount);
                model.chainWave(tailParts, speed_walk, degree_walk * 0.15F, 2, limbSwing, limbSwingAmount);
                model.chainSwing(neckParts, speed_walk, degree_walk * 0.15F, 2, limbSwing, limbSwingAmount);
                model.chainWave(neckParts, speed_walk, degree_walk * 0.05F, -2, limbSwing, limbSwingAmount);
                model.chainSwing(tailParts, speed_idle, degree_idle * 0.25F, -2, ageInTicks, 1);
                model.chainWave(tailParts, speed_idle, degree_idle * 0.15F, -2, ageInTicks, 1);
                model.chainWave(neckParts, speed_idle, degree_idle * -0.15F, -3, ageInTicks, 1);
                model.walk(model.getCube("Neck1"), speed_idle, degree_idle * 0.05F, false, 0, 0, ageInTicks, 1);
            }
            model.bob(model.getCube("BodyUpper"), speed_idle, degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("ThighR"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("ThighL"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("armR1"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            model.bob(model.getCube("armL1"), speed_idle, -degree_idle * 1.3F, false, ageInTicks, 1);
            if (entity.getAnimation() != EntityDragonBase.ANIMATION_SHAKEPREY || entity.getAnimation() != EntityDragonBase.ANIMATION_ROAR) {
                model.faceTarget(rotationYaw, rotationPitch, 4, neckParts);
            }
            if (entity.isActuallyBreathingFire()) {
                float speed_shake = 0.7F;
                float degree_shake = 0.1F;
                model.chainFlap(neckParts, speed_shake, degree_shake, 2, ageInTicks, 1);
                model.chainSwing(neckParts, speed_shake * 0.65F, degree_shake * 0.1F, 1, ageInTicks, 1);
            }
        }
        if (!entity.isModelDead()) {
            if (entity.turn_buffer != null && !(entity.isBeingRidden() && !entity.isPassenger() && entity.isBreathingFire())) {
                entity.turn_buffer.applyChainSwingBuffer(neckParts);
            }
            if (entity.tail_buffer != null && !entity.isPassenger()) {
                entity.tail_buffer.applyChainSwingBuffer(tailPartsWBody);
            }
            if (entity.roll_buffer != null && entity.pitch_buffer_body != null && entity.pitch_buffer != null) {
                if (entity.flyProgress > 0 || entity.hoverProgress > 0) {
                    entity.roll_buffer.applyChainFlapBuffer(model.getCube("BodyUpper"));
                    entity.pitch_buffer_body.applyChainWaveBuffer(model.getCube("BodyUpper"));
                    entity.pitch_buffer.applyChainWaveBufferReverse(tailPartsWBody);
                }
            }
        }
        if (entity.getWidth() >= 2 && entity.flyProgress == 0 && entity.hoverProgress == 0) {
            LegArticulator.articulateQuadruped(entity, entity.legSolver, model.getCube("BodyUpper"), model.getCube("BodyLower"), model.getCube("Neck1"),
                    model.getCube("ThighL"), model.getCube("LegL"), toesPartsL,
                    model.getCube("ThighR"), model.getCube("LegR"), toesPartsR,
                    model.getCube("armL1"), model.getCube("armL2"), clawL,
                    model.getCube("armR1"), model.getCube("armR2"), clawR,
                    1.0F, 0.5F, 0.5F, -0.15F, -0.15F, 0F,
                    Minecraft.getInstance().getRenderPartialTicks()
            );
        }
    }

    private void genderMob(EntityIceDragon entity, AdvancedModelBox cube) {
        if (!entity.isMale()) {
            TabulaModel maleModel = EnumDragonAnimations.MALE.icedragon_model;
            TabulaModel femaleModel = EnumDragonAnimations.FEMALE.icedragon_model;
            float x = femaleModel.getCube(cube.boxName).rotateAngleX;
            float y = femaleModel.getCube(cube.boxName).rotateAngleY;
            float z = femaleModel.getCube(cube.boxName).rotateAngleZ;
            if (x != maleModel.getCube(cube.boxName).rotateAngleX || y != maleModel.getCube(cube.boxName).rotateAngleY || z != maleModel.getCube(cube.boxName).rotateAngleZ) {
                this.setRotateAngle(cube, 1F, x, y, z);
            }
        }
    }

    private boolean isWing(TabulaModel model, AdvancedModelBox modelRenderer) {
        return model.getCube("armL1") == modelRenderer || model.getCube("armR1") == modelRenderer || model.getCube("armL1").childModels.contains(modelRenderer) || model.getCube("armR1").childModels.contains(modelRenderer);
    }

    private boolean isHorn(AdvancedModelBox modelRenderer) {
        return modelRenderer.boxName.contains("Horn");
    }

    public void animate(TabulaModel model, EntityIceDragon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.llibAnimator.update(entity);
        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_FIRECHARGE);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.BLAST_CHARGE1.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.BLAST_CHARGE2.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.BLAST_CHARGE3.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(5);
        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_SPEAK);
        model.llibAnimator.startKeyframe(5);
        this.rotate(model.llibAnimator, model.getCube("Jaw"), 18, 0, 0);
        model.llibAnimator.move(model.getCube("Jaw"), 0, 0, 0.2F);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.setStaticKeyframe(5);
        model.llibAnimator.startKeyframe(5);
        this.rotate(model.llibAnimator, model.getCube("Jaw"), 18, 0, 0);
        model.llibAnimator.move(model.getCube("Jaw"), 0, 0, 0.2F);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(5);

        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_BITE);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.BITE1.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.BITE2.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.BITE3.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);

        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_SHAKEPREY);
        model.llibAnimator.startKeyframe(15);
        moveToPose(model, EnumDragonAnimations.GRAB1.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.GRAB2.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.GRAB_SHAKE1.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.GRAB_SHAKE2.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.GRAB_SHAKE3.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);
        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_TAILWHACK);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.TAIL_WHIP1.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.TAIL_WHIP2.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.TAIL_WHIP3.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);
        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_WINGBLAST);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.WING_BLAST1.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.WING_BLAST2.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.WING_BLAST3.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.WING_BLAST4.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.WING_BLAST5.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.WING_BLAST6.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumDragonAnimations.WING_BLAST5.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -4F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);
        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_ROAR);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.ROAR1.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.ROAR2.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.ROAR3.icedragon_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);

        model.llibAnimator.setAnimation(EntityIceDragon.ANIMATION_EPIC_ROAR);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.EPIC_ROAR1.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -6.8F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.EPIC_ROAR2.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -6.8F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.EPIC_ROAR3.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -6.8F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.EPIC_ROAR2.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -6.8F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumDragonAnimations.EPIC_ROAR3.icedragon_model);
        model.llibAnimator.move(model.getCube("BodyUpper"), 0, -6.8F, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);
    }


}
