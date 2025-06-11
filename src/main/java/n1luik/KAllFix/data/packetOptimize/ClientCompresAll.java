package n1luik.KAllFix.data.packetOptimize;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ClientCompresAll {
    public static ClientGamePacketListener listener;
    public static void setImpl() {
        if (Boolean.getBoolean("KAF-packetOptimize.CompatibilityMode.ClientboundBlockEntityDataPacket")) {
            if (Boolean.getBoolean("KAF-packetOptimize.CompatibilityMode.ClientboundSectionBlocksUpdatePacket")){
                ClientboundCompress1Packet.clientCall = ClientCompresAll::Compress1_3;
            }else {
                ClientboundCompress1Packet.clientCall = ClientCompresAll::Compress1_1;
            }
        }else {
            if (Boolean.getBoolean("KAF-packetOptimize.CompatibilityMode.ClientboundSectionBlocksUpdatePacket")){
                ClientboundCompress1Packet.clientCall = ClientCompresAll::Compress1_2;
            }else {
                ClientboundCompress1Packet.clientCall = ClientCompresAll::Compress1_0;
            }

        }

    }
    public static void Compress1_0(ClientboundCompress1Packet p){

        if (p.data != null) {
            ClientCompresPacketLoader clientCompresPacketLoader = new ClientCompresPacketLoader(new DataInputStream(new ByteArrayInputStream(p.data)));
            try {
                clientCompresPacketLoader.start();
            } catch (IOException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            Util.backgroundExecutor().execute(()->{
                try {
                    exec(clientCompresPacketLoader.out, clientCompresPacketLoader::isStop);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    public static void Compress1_1(ClientboundCompress1Packet p){

        if (p.data != null) {
            CompatibilityMode1ClientCompresPacketLoader clientCompresPacketLoader = new CompatibilityMode1ClientCompresPacketLoader(p.data);
            try {
                clientCompresPacketLoader.start();
            } catch (IOException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            Util.backgroundExecutor().execute(()->{
                try {
                    exec(clientCompresPacketLoader.out, clientCompresPacketLoader::isStop);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    public static void Compress1_2(ClientboundCompress1Packet p){

        if (p.data != null) {
            CompatibilityMode2ClientCompresPacketLoader clientCompresPacketLoader = new CompatibilityMode2ClientCompresPacketLoader(p.data);
            try {
                clientCompresPacketLoader.start();
            } catch (IOException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            Util.backgroundExecutor().execute(()->{
                try {
                    exec(clientCompresPacketLoader.out, clientCompresPacketLoader::isStop);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    public static void Compress1_3(ClientboundCompress1Packet p){

        if (p.data != null) {
            CompatibilityMode3ClientCompresPacketLoader clientCompresPacketLoader = new CompatibilityMode3ClientCompresPacketLoader(p.data);
            try {
                clientCompresPacketLoader.start();
            } catch (IOException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            Util.backgroundExecutor().execute(()->{
                try {
                    exec(clientCompresPacketLoader.out, clientCompresPacketLoader::isStop);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static void exec(List<Packet<ClientGamePacketListener>> exec, BooleanSupplier end) throws InterruptedException {
        Minecraft instance = Minecraft.getInstance();
        ClientGamePacketListener listener1 = listener;
        while (true){
            while (!exec.isEmpty()){
                Packet<ClientGamePacketListener> remove = exec.remove(0);
                instance.execute(()->remove.handle(listener1));
            }
            if (end.getAsBoolean()) return;
            Thread.sleep(55);
        }
    }
}

