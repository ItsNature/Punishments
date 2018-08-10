package gg.nature.punishments.commands.handle.warn;

import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.StringJoiner;
import java.util.stream.IntStream;

public class WarnCommand extends BaseCommand {

    public WarnCommand() {
        super("warn","punish.warn");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.WARN_USAGE);
            return;
        }

        StringJoiner joiner = new StringJoiner(" ");
        IntStream.range(1, args.length).forEach(i -> joiner.add(args[i]));
        String reason = joiner.toString();

        if(!Utils.isReasonValid(reason)) {
            sender.sendMessage(Language.WARN_USAGE);
            return;
        }

        new Punishment(PunishmentType.WARN, sender.getName(), Bukkit.getOfflinePlayer(args[0]).getName(), System.currentTimeMillis(), reason, Utils.PERMANENT, true, false, Config.SERVER_NAME);
    }
}
