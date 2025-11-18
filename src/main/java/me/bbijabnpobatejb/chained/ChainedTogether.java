package me.bbijabnpobatejb.chained;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.chained.command.ChainCommand;
import me.bbijabnpobatejb.chained.entity.chained.ChainHandler;
import me.bbijabnpobatejb.chained.packet.PacketHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChainedTogether implements ModInitializer {
    public static final String MOD_ID = "chained-together";
    public static final String MOD_NAME = "Chained Together";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ChainedTogether instance;

    @Override
    public void onInitialize() {
        instance = this;
        ChainHandler.register();
        ChainCommand.register();
        sendConsole("Mod has been successfully started");
        PacketHandler.register();
    }




    public static void sendConsole(String msg) {
        LOGGER.info("[" + MOD_NAME + "] {}", msg);
    }

    public static void sendChat(String msg) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.player.sendMessage(new LiteralText(msg), false);
    }


}
