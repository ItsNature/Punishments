package gg.nature.punishments.commands.handle;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PunishRollbackCommand extends BaseCommand {

    public PunishRollbackCommand() {
        super("punishrollback", Arrays.asList("staffrollback", "srollback", "prollback"), "punish.punishrollback");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.PUNISHROLLBACK_USAGE);
            return;
        }

        int amount = 0;

        if(!Utils.isInteger(args[1]) && !args[1].equalsIgnoreCase("all")) {
            sender.sendMessage(Language.INVALID_NUMBER.replace("<argument>", args[1]));
            return;
        }

        if(Utils.isInteger(args[1])) amount = Integer.parseInt(args[1]);

        boolean silent = false;

        if(args.length == 3 && args[2].toLowerCase().contains("-s")) silent = true;

        final boolean newSilent = silent;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());
        List<String> punished = data.getPunished();

        if(args[1].equalsIgnoreCase("all")) amount = punished.size();

        if(amount > punished.size()) {
            sender.sendMessage(Language.PUNISHROLLBACK_INVALID_AMOUNT.replace("<player>", target.getName()));
            return;
        }

        List<String> toPunish = new ArrayList<>(punished.subList(punished.size() - amount, punished.size()));

        toPunish.forEach(string -> {
            String name = string.split("/")[0];
            PunishmentType type = PunishmentType.valueOf(string.split("/")[1].split("-")[0]);

            if(type != PunishmentType.KICK) {
                Utils.dispatchCommand(Utils.getByType(type, true) + " " + name + " ROLLBACK" + (newSilent ? " -s" : ""));
            }

            data.getPunished().remove(string);
        });

        if(!target.isOnline()) data.saveAsync();

        sender.sendMessage(Language.PUNISHROLLBACK_SUCCESS.replace("<amount>", String.valueOf(amount)).replace("<player>", target.getName()));
    }
}
