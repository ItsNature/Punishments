package gg.nature.punishments.commands.handle.blacklist;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.StringJoiner;
import java.util.stream.IntStream;

public class BlacklistCommand extends BaseCommand {

    public BlacklistCommand() {
        super("blacklist","punish.blacklist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.BLACKLIST_USAGE);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Punishment punishment = Utils.getPunishment(Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName()), PunishmentType.BLACKLIST);

        if(punishment != null) {
            sender.sendMessage(Language.ALREADY_BLACKLISTED.replace("<player>", target.getName()));
            return;
        }

        StringJoiner joiner = new StringJoiner(" ");
        IntStream.range(1, args.length).forEach(i -> joiner.add(args[i]));
        String reason = joiner.toString();

        if(!Utils.isReasonValid(reason)) {
            sender.sendMessage(Language.BLACKLIST_USAGE);
            return;
        }

        new Punishment(PunishmentType.BLACKLIST, sender.getName(), target.getName(), System.currentTimeMillis(), reason, Utils.PERMANENT, true, false, Config.SERVER_NAME);
    }
}
