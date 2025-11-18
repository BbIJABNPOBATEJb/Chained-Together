package me.bbijabnpobatejb.chained.util;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@UtilityClass
public class WorldUtil {


    public Identifier getWorld(Entity entity) {
        return entity.world.getRegistryKey().getValue();
    }
}
