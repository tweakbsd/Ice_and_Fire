package com.github.alexthe666.iceandfire.event;

import java.util.UUID;
import java.util.Map;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.text.ITextComponent;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderEvents {
    public ResourceLocation redTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_fire.png");
    public ResourceLocation redElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_fire.png");
    public ResourceLocation blueTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_ice.png");
    public ResourceLocation blueElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_ice.png");
    public ResourceLocation betaTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_beta.png");
    public ResourceLocation betaElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_beta.png");

    public UUID[] redcapes = new UUID[]{
            /* zeklo */UUID.fromString("59efccaf-902d-45da-928a-5a549b9fd5e0"),
            /* Alexthe666 */UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c"),
            /* tweakbsd */ UUID.fromString("7794afc9-f3d8-4b90-8a27-e8548f1d0f84")
    };
    public UUID[] bluecapes = new UUID[]{
            /* Raptorfarian */UUID.fromString("0ed918c8-d612-4360-b711-cd415671356f"),
            /*Zyranna*/        UUID.fromString("5d43896a-06a0-49fb-95c5-38485c63667f")};
    public UUID[] betatesters = new UUID[]{
    };

    public UUID tweakbsd = UUID.fromString("7794afc9-f3d8-4b90-8a27-e8548f1d0f84");

    /*

    */

    @SubscribeEvent
    public void playerRender(RenderPlayerEvent.Pre event) {

        // NOTE: tweakbsd fixed cape rendering and added myself a red cape
        if (event.getEntityLiving() instanceof AbstractClientPlayerEntity) {
                NetworkPlayerInfo info = ((AbstractClientPlayerEntity)event.getEntityLiving()).getPlayerInfo();
            if (info != null) {
                Map<MinecraftProfileTexture.Type, ResourceLocation> textureMap = info.playerTextures;
                if (textureMap != null) {
                    if(event.getEntityLiving().getUniqueID().equals(tweakbsd)) {

                        textureMap.put(MinecraftProfileTexture.Type.CAPE, redTex);
                        textureMap.put(MinecraftProfileTexture.Type.ELYTRA, redElytraTex);

                    } else if (hasBetaCape(event.getEntityLiving().getUniqueID())) {

                        textureMap.put(MinecraftProfileTexture.Type.CAPE, betaTex);
                        textureMap.put(MinecraftProfileTexture.Type.ELYTRA, betaElytraTex);

                    } else if (hasRedCape(event.getEntityLiving().getUniqueID())) {

                        textureMap.put(MinecraftProfileTexture.Type.CAPE, redTex);
                        textureMap.put(MinecraftProfileTexture.Type.ELYTRA, redElytraTex);

                    } else if (hasBlueCape(event.getEntityLiving().getUniqueID())) {
                        textureMap.put(MinecraftProfileTexture.Type.CAPE, blueTex);
                        textureMap.put(MinecraftProfileTexture.Type.ELYTRA, blueElytraTex);
                    }
                }
            }
        }
        // NOTE: twekbsd removed ... renders an item above your head
        /*
        if (event.getEntityLiving().getScoreboardName().contains("tweakbsd")) {
            event.getMatrixStack().push();
            float f2 = ((float) event.getEntityLiving().ticksExisted - 1 + event.getPartialRenderTick());
            float f3 = MathHelper.sin(f2 / 10.0F) * 0.1F + 0.1F;
            event.getMatrixStack().translate((float) 0, event.getEntityLiving().getHeight() * 1.25F, (float) 0);
            float f4 = (f2 / 20.0F) * (180F / (float) Math.PI);
            event.getMatrixStack().rotate(new Quaternion(Vector3f.YP, f4, true));
            event.getMatrixStack().push();
            Minecraft.getInstance().getItemRenderer().renderItem(Minecraft.getInstance().player, new ItemStack(IafItemRegistry.FIRE_DRAGON_BLOOD), ItemCameraTransforms.TransformType.GROUND, false, event.getMatrixStack(), event.getBuffers(), event.getEntityLiving().world, event.getLight(), OverlayTexture.NO_OVERLAY);
            event.getMatrixStack().pop();
            event.getMatrixStack().pop();

        }
         */
    }


    // NOTE: tweakbsd disabled all cape code
    private boolean hasRedCape(UUID uniqueID) {
        /*
        for (UUID uuid1 : redcapes) {
            if (uniqueID.equals(uuid1)) {
                return true;
            }
        }
         */
        return false;
    }

    private boolean hasBlueCape(UUID uniqueID) {

        /*
        for (UUID uuid1 : bluecapes) {
            if (uniqueID.equals(uuid1)) {
                return true;
            }
        }
         */
        return false;
    }

    private boolean hasBetaCape(UUID uniqueID) {
        /*
        for (UUID uuid1 : betatesters) {
            if (uniqueID.equals(uuid1)) {
                return true;
            }
        }
         */
        return false;
    }
}
