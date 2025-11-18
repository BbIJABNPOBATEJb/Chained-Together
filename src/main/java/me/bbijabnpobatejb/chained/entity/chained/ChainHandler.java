package me.bbijabnpobatejb.chained.entity.chained;

import com.ibm.icu.impl.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.chained.packet.PacketHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.*;

@UtilityClass
public class ChainHandler {
    public final Set<Pair<UUID, UUID>> CHAINED_ENTITIES = new HashSet<>();
    public final HashMap<UUID, Identifier> LAST_DIMENSION = new HashMap<>();
    public static double CHAIN_DISTANCE = 6.0;

    public void register() {
        ServerTickEvents.END_SERVER_TICK.register(ChainHandler::endServerTick);
    }

    boolean checkEntity(Entity entity1) {
        val value = entity1.world.getRegistryKey().getValue();
        val uuid = entity1.getUuid();
        val last = LAST_DIMENSION.getOrDefault(uuid, null);
        LAST_DIMENSION.put(uuid, value);
        return last != null && !last.equals(value);
    }

    public void updateChainedPlayers(Entity entity1, Entity entity2) {
        if (!entity1.isAlive() || !entity2.isAlive()) return;
        if (checkEntity(entity1)) {
            PacketHandler.sendChainedPacketToClient();
        }
        if (checkEntity(entity2)) {
            PacketHandler.sendChainedPacketToClient();
        }
        if (!entity1.world.equals(entity2.world)) return;

        val distance = entity1.getPos().distanceTo(entity2.getPos());
        if (distance > CHAIN_DISTANCE * 2) return;

        if (distance > CHAIN_DISTANCE) {
            val direction = entity2.getPos().subtract(entity1.getPos()).normalize();
            val pullStrength = 0.05;

            val velocity1 = direction.multiply(pullStrength);
            val velocity2 = direction.multiply(-pullStrength);

            entity1.addVelocity(velocity1.x, velocity1.y, velocity1.z);
            entity2.addVelocity(velocity2.x, velocity2.y, velocity2.z);


            entity1.velocityModified = true;
            entity2.velocityModified = true;
        }

    }


    void endServerTick(MinecraftServer server) {

        Map<UUID, Entity> entityCache = new HashMap<>();

        for (val world : server.getWorlds()) {
            for (val entity : world.iterateEntities()) {
                entityCache.put(entity.getUuid(), entity);
            }
        }

        CHAINED_ENTITIES.removeIf(pair -> {
            val entity1 = entityCache.get(pair.first);
            val entity2 = entityCache.get(pair.second);

            if (entity1 != null && entity2 != null) {
                updateChainedPlayers(entity1, entity2);
                return false;
            }

            return true;
        });
    }


}
