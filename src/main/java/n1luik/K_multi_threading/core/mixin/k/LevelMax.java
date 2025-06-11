package n1luik.K_multi_threading.core.mixin.k;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({
        ForceLoadCommand.class,
        Level.class,
        DedicatedServerProperties.class,
        WorldBorder.class,
        MinecraftServer.class,
        Player.class,
        WorldBorderCommand.class
        /*, LevelReader.class*/})
public class LevelMax {
    @ModifyConstant(method = "*",constant = @Constant(intValue = -30000000))
    public int reload(int v){
        return Integer.MIN_VALUE;
    }
    @ModifyConstant(method = "*",constant = @Constant(intValue = 30000000))
    public int reload2(int v){
        return Integer.MAX_VALUE;
    }


    @ModifyConstant(method = "*",constant = @Constant(intValue = -29999984))
    public int reload3(int v){
        return Integer.MIN_VALUE+10;
    }
    @ModifyConstant(method = "*",constant = @Constant(intValue = 29999984))
    public int reload4(int v){
        return Integer.MAX_VALUE-10;
    }


    @ModifyConstant(method = "*",constant = @Constant(intValue = -29999999))
    public int reload5(int v){
        return Integer.MIN_VALUE+10;
    }
    @ModifyConstant(method = "*",constant = @Constant(intValue = 29999999))
    public int reload6(int v){
        return Integer.MAX_VALUE-10;
    }


    @ModifyConstant(method = "*",constant = @Constant(doubleValue = -2.9999999E7D))
    public double reload7(double v){
        return Integer.MIN_VALUE+10;
    }
    @ModifyConstant(method = "*",constant = @Constant(doubleValue = 2.9999999E7D))
    public double reload8(double v){
        return Integer.MAX_VALUE-10;
    }


    @ModifyConstant(method = "*",constant = @Constant(doubleValue = -5.999997E7F))
    public double reload9(double v){
        return Integer.MIN_VALUE+10;
    }
    @ModifyConstant(method = "*",constant = @Constant(doubleValue = 5.999997E7F))
    public double reload10(double v){
        return Integer.MAX_VALUE-10;
    }


    @ModifyConstant(method = "*",constant = @Constant(doubleValue = -2.9999984E7D))
    public double reload11(double v){
        return Integer.MIN_VALUE+10;
    }
    @ModifyConstant(method = "*",constant = @Constant(doubleValue = 2.9999984E7D))
    public double reload12(double v){
        return Integer.MAX_VALUE-10;
    }
}
