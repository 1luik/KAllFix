package n1luik.KAllFix.forge.LoginProtectionMod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent.LoginProtection;

public class ClientboundRemoveLoginProtectionPacket {
    //public static Consumer<ClientboundRemoveLoginProtectionPacket> clientCall;

    public ClientboundRemoveLoginProtectionPacket(){
    }

    public ClientboundRemoveLoginProtectionPacket(FriendlyByteBuf pack){
    }

    public void write(FriendlyByteBuf pack) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        //clientCall.accept(this);
        LoginProtection = false;
        contextSupplier.get().setPacketHandled(true);

    }
}

