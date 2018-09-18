package gg.nature.punishments.commands.handle.ban;

import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public class BanCommand extends BaseCommand {

    public BanCommand() {
        super("ban", Arrays.asList("tempban", "tban"), "punish.ban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.BAN_USAGE);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if(!Utils.isPermissible(sender, target)) {
            sender.sendMessage(Language.NO_PERMISSION);
            return;
        }

        if(Utils.getPunishment(target, PunishmentType.BAN) != null) {
            sender.sendMessage(Language.ALREADY_BANNED.replace("<player>", target.getName()));
            return;
        }

        long duration = Utils.getDuration(args[1].toLowerCase());
        StringJoiner joiner = new StringJoiner(" ");
        IntStream.range(duration == Utils.PERMANENT ? 1 : 2, args.length).forEach(i -> joiner.add(args[i]));
        String reason = joiner.toString();

        if(!Utils.isReasonValid(reason)) {
            sender.sendMessage(Language.BAN_USAGE);
            return;
        }

        new Punishment(PunishmentType.BAN, sender.getName(), target.getName(), System.currentTimeMillis(), reason, duration, true, false, Config.SERVER_NAME);
    }
}
