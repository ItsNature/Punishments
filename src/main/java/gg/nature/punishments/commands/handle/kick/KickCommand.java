package gg.nature.punishments.commands.handle.kick;

import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;
import java.util.stream.IntStream;

public class KickCommand extends BaseCommand {

    public KickCommand() {
        super("kick","punish.kick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.KICK_USAGE);
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null) {
            sender.sendMessage(Language.PLAYER_NOT_ONLINE.replace("<player>", args[0]));
            return;
        }


        StringJoiner joiner = new StringJoiner(" ");
        IntStream.range(1, args.length).forEach(i -> joiner.add(args[i]));
        String reason = joiner.toString();

        if(!Utils.isReasonValid(reason)) {
            sender.sendMessage(Language.KICK_USAGE);
            return;
        }

        new Punishment(PunishmentType.KICK, sender.getName(), player.getName(), System.currentTimeMillis(), reason, Utils.PERMANENT, true, false, Config.SERVER_NAME);
    }
}
