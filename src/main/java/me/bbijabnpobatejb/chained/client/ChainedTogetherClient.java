package me.bbijabnpobatejb.chained.client;

import lombok.Getter;
import me.bbijabnpobatejb.chained.ChainedTogether;
import me.bbijabnpobatejb.chained.packet.PacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static me.bbijabnpobatejb.chained.ChainedTogether.*;

public class ChainedTogetherClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
    }

}
