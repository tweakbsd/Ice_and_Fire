package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.serialization.Codec;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.Heightmap;;

public class GraveyardStructure extends Structure<NoFeatureConfig> {

    public GraveyardStructure(Codec<NoFeatureConfig> p_i51440_1_) {
        super(p_i51440_1_);
        this.setRegistryName("iceandfire:graveyard");
    }

    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public String getStructureName() {
        return IceAndFire.MODID + ":graveyard";
    }

    public IStartFactory getStartFactory() {
        return GraveyardStructure.Start::new;
    }

   /* 
    public int getSize() {
        return 3;
    }

    protected int getSeedModifier() {
        return 123456789;
    }

    protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGenerator) {
        return 8;// Math.max(IafConfig.spawnGorgonsChance, 2);
    }

    protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGenerator) {
        return 4; //Math.max(IafConfig.spawnGorgonsChance / 2, 1);
    }*/

    public static class Start extends StructureStart {
        public Start(Structure<?> p_i225817_1_, int p_i225817_2_, int p_i225817_3_, MutableBoundingBox p_i225817_4_, int p_i225817_5_, long p_i225817_6_) {
            super(p_i225817_1_, p_i225817_2_, p_i225817_3_, p_i225817_4_, p_i225817_5_, p_i225817_6_);
        }

        @Override
        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int x, int z, Biome p_230364_6_, IFeatureConfig p_230364_7_) {
            Rotation rotation = Rotation.randomRotation(this.rand);
            int i = 5;
            int j = 5;
            if (rotation == Rotation.CLOCKWISE_90) {
               i = -5;
            } else if (rotation == Rotation.CLOCKWISE_180) {
               i = -5;
               j = -5;
            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
               j = -5;
            }

            int k = (x << 4) + 7;
            int l = (z << 4) + 7;
            int i1 = p_230364_2_.getNoiseHeightMinusOne(k, l, Heightmap.Type.WORLD_SURFACE_WG);
            int j1 = p_230364_2_.getNoiseHeightMinusOne(k, l + j, Heightmap.Type.WORLD_SURFACE_WG);
            int k1 = p_230364_2_.getNoiseHeightMinusOne(k + i, l, Heightmap.Type.WORLD_SURFACE_WG);
            int l1 = p_230364_2_.getNoiseHeightMinusOne(k + i, l + j, Heightmap.Type.WORLD_SURFACE_WG);
            int i2 = Math.min(Math.min(i1, j1), Math.min(k1, l1));
            if (i2 >= 60) {
                BlockPos blockpos = new BlockPos(x * 16 + 8, i2 + 1, z * 16 + 8);
	            GraveyardPiece.func_204760_a(p_230364_3_, blockpos, rotation, this.components, this.rand);
	            this.recalculateStructureSize();
            }
        }
    }

}
