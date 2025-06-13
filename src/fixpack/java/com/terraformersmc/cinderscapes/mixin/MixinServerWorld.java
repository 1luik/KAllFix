package com.terraformersmc.cinderscapes.mixin;

import com.terraformersmc.cinderscapes.config.CinderscapesConfig;
import com.terraformersmc.cinderscapes.init.CinderscapesBiomes;
import com.terraformersmc.cinderscapes.init.CinderscapesBlocks;
import com.terraformersmc.cinderscapes.tag.CinderscapesBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 这里的代码没有使用是想修复包用来编译的
 * <p>
 * 原名MixinServerWorld
 * */
@Mixin(ServerLevel.class)
public abstract class MixinServerWorld extends Level {
    
    @Unique
    public boolean KAllFix$isAshFallEnabled = false;

    protected MixinServerWorld(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }


    @Inject(method="<init>", at = @At("RETURN"), remap = false)//, locals = LocalCapture.CAPTURE_FAILHARD)
    private void init(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci){
        KAllFix$isAshFallEnabled = dimensionTypeId().location().equals(BuiltinDimensionTypes.NETHER.location());
    }

    @Inject(method="tickChunk", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;m_204166_(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;", ordinal = 0, remap = false))//, locals = LocalCapture.CAPTURE_FAILHARD)
    private void cinderscapes$tickChunk(LevelChunk chunk, int p_8716_, CallbackInfo ci) {
        if (CinderscapesConfig.INSTANCE.enableAshFall && KAllFix$isAshFallEnabled) {
            ChunkPos pos1 = chunk.getPos();
            BlockPos pos = this.getBlockRandomPos(pos1.getMinBlockX(), 0, pos1.getMinBlockX(), 15);
            BlockState state = getBlockState(pos);
            Holder<Biome> biome = this.getBiome(pos);

            while (!(
                    biome.is(CinderscapesBiomes.ASHY_SHOALS) &&
                            state.isFaceSturdy(this, pos, Direction.UP) &&
                            blockAbove(pos).is(CinderscapesBlockTags.ASH_PERMEABLE) &&
                            this.getBlockState(pos.above()).isAir() &&
                            CinderscapesBlocks.ASH.defaultBlockState().canSurvive(this, pos.above())) &&
                    pos.getY() < 127) {
                pos = pos.above();
                state = getBlockState(pos);
                biome = this.getBiome(pos);
            }
            if (pos.getY() < 127) this.setBlockAndUpdate(pos.above(), CinderscapesBlocks.ASH.defaultBlockState());
        }
    }

    private BlockState blockAbove(BlockPos pos) {
        BlockPos iPos = pos.mutable();
        while(isEmptyBlock(iPos.above()) && iPos.getY() < 127) {
            iPos = iPos.above();
        }
        return getBlockState(iPos.above());
    }
}
