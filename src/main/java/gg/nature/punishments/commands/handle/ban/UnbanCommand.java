package gg.nature.punishments.commands.handle.ban;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public class UnbanCommand extends BaseCommand {

    public UnbanCommand() {
        super("unban", Collections.singletonList("pardon"), "punish.unban");

        this.async = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.UNBAN_USAGE);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

        if(Utils.getPunishment(data, PunishmentType.BAN) == null) {
            sender.sendMessage(Language.NOT_BANNED.replace("<player>", target.getName()));
            return;
        }

        StringJoiner joiner = new StringJoiner(" ");
        IntStream.range(1, args.length).forEach(i -> joiner.add(args[i]));
        String reason = joiner.toString();

        if(!Utils.isReasonValid(reason)) {
            sender.sendMessage(Language.UNBAN_USAGE);
            return;
        }

        Punishments.getInstance().getPunishDataManager().unpunish(Config.BUNGEE, PunishmentType.BAN , target, sender.getName(), reason, data);
    }
}
