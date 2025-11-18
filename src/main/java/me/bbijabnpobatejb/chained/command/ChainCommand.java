package me.bbijabnpobatejb.chained.command;

import com.ibm.icu.impl.Pair;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.bbijabnpobatejb.chained.entity.chained.ChainHandler;
import me.bbijabnpobatejb.chained.packet.PacketHandler;
import me.bbijabnpobatejb.chained.util.StringUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChainCommand {


    final String TARGET = "target";
    final String TARGET_2 = "target2";
    final String PREFIX = "chain";
    final String UNCHAIN = "unchain";
    final String DISTANCE = "distance";
    final String CHAINDISTANCE = "chaindistance";

    public void register() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal(PREFIX)
                    .then(argument(TARGET, EntityArgumentType.entity())
                            .executes(ctx -> chain(ctx, null)))
                    .then(argument(TARGET, EntityArgumentType.entity())
                            .then(argument(TARGET_2, EntityArgumentType.entity())
                                    .executes(ctx -> chain(ctx, TARGET_2)))));

            dispatcher.register(literal(UNCHAIN)
                    .executes(ChainCommand::unchain));
            dispatcher.register(literal(CHAINDISTANCE)
                    .then(argument(DISTANCE, DoubleArgumentType.doubleArg())
                            .executes(ChainCommand::chainDistance)));
        });
    }

    int chain(CommandContext<ServerCommandSource> ctx, @Nullable String secondTargetName) throws CommandSyntaxException {
        Entity targetA;
        Entity targetB;

        if (secondTargetName == null) {
            targetA = ctx.getSource().getEntityOrThrow();
            targetB = EntityArgumentType.getEntity(ctx, TARGET);
        } else {
            targetA = EntityArgumentType.getEntity(ctx, TARGET);
            targetB = EntityArgumentType.getEntity(ctx, secondTargetName);
        }

        if (targetA == targetB) {
            ctx.getSource().sendFeedback(new LiteralText("§cНельзя связать сущность саму с собой!"), false);
            return 0;
        }

        val distance = targetA.distanceTo(targetB);
        val max = ChainHandler.CHAIN_DISTANCE * 2;
        if (distance > max) {
            val current = StringUtil.formatDouble(distance, 1);
            ctx.getSource().sendFeedback(new LiteralText("§cСлишком далеко. Максимальная дистанция " + max + ", текущая " + current), false);
            return 0;
        }

        ChainHandler.CHAINED_ENTITIES.add(Pair.of(targetA.getUuid(), targetB.getUuid()));
        PacketHandler.sendChainedPacketToClient();
        ctx.getSource().sendFeedback(
                new LiteralText("§aСвязаны: " + targetA.getName().getString() + " ↔ " + targetB.getName().getString()
                        + "§7. Чтобы отвязаться, используйте §e/unchain"),
                false
        );

        return Command.SINGLE_SUCCESS;
    }

    int unchain(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        val executor = ctx.getSource().getEntityOrThrow();
        val uuid = executor.getUuid();
        val removed = ChainHandler.CHAINED_ENTITIES.removeIf(pair -> pair.first.equals(uuid) || pair.second.equals(uuid));
        PacketHandler.sendChainedPacketToClient();

        if (removed) {
            ctx.getSource().sendFeedback(new LiteralText("§aВы отвязались от всех связей."), false);
            return Command.SINGLE_SUCCESS;
        }

        ctx.getSource().sendFeedback(new LiteralText("§eВы не были ни с кем связаны."), false);
        return 0;
    }

    int chainDistance(CommandContext<ServerCommandSource> ctx) {
        val v = DoubleArgumentType.getDouble(ctx, DISTANCE);
        ChainHandler.CHAIN_DISTANCE = v;
        ctx.getSource().sendFeedback(new LiteralText("§aДистанция цепи установлена на " + v + "."), false);
        return 0;
    }

    public void unchainAll() {
        ChainHandler.CHAINED_ENTITIES.clear();
        PacketHandler.sendChainedPacketToClient();
    }

    public void removeChainBetween(Entity a, Entity b) {
        val firstUuid = a.getUuid();
        val secondUuid = b.getUuid();
        ChainHandler.CHAINED_ENTITIES.removeIf(pair ->
                checkEquals(pair, firstUuid, secondUuid) || checkEquals(pair, secondUuid, firstUuid)
        );
        PacketHandler.sendChainedPacketToClient();
    }

    boolean checkEquals(Pair<UUID, UUID> pair, UUID firstUuid, UUID secondUuid) {
        return pair.first.equals(firstUuid) && pair.second.equals(secondUuid);
    }
}