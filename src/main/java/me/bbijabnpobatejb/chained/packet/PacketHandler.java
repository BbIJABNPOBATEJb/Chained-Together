package me.bbijabnpobatejb.chained.packet;

import com.ibm.icu.impl.Pair;
import io.netty.buffer.Unpooled;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.chained.client.render.ChainRender;
import me.bbijabnpobatejb.chained.entity.chained.ChainHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.bbijabnpobatejb.chained.ChainedTogether.MOD_ID;

@UtilityClass
public class PacketHandler {
    MinecraftServer server;
    public final Identifier CHANNEL = new Identifier(MOD_ID, "main");

    public void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PacketHandler.server = server;
        });


        ClientSidePacketRegistry.INSTANCE.register(CHANNEL, (context, buf) -> {
            Set<Pair<Integer, Integer>> pairs = new HashSet<>();
            val size = buf.readInt();
            for (int i = 0; i < size; i++) {
                pairs.add(Pair.of(buf.readInt(), buf.readInt()));
            }
            ChainRender.setClientRenderChainedEntities(pairs);
        });

    }


    public void sendChainedPacketToClient() {
        for (val player : server.getPlayerManager().getPlayerList()) {
            sendChainedPacketToClient(player);
        }
    }

    public void sendChainedPacketToClient(ServerPlayerEntity player) {
        val set = ChainHandler.CHAINED_ENTITIES;
        val buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(set.size());
        for (val pair : set) {
            val first = pair.first;
            val second = pair.second;
            val entityId = getEntityId(first);
            buf.writeInt(entityId);
            val entityId1 = getEntityId(second);
            buf.writeInt(entityId1);
        }
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, CHANNEL, buf);
    }


    int getEntityId(UUID uuid) {
        for (ServerWorld world : server.getWorlds()) {
            val entity = world.getEntity(uuid);
            if (entity != null) {
                return entity.getEntityId();
            }
        }
        return -1;
    }

}
