package gg.nature.punishments.commands.handle;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.data.PunishedData;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Tasks;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PunishRollbackCommand extends BaseCommand {

    public PunishRollbackCommand() {
        super("punishrollback", Arrays.asList("staffrollback", "srollback"), "punish.punishrollback");

        this.async = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.PUNISHROLLBACK_USAGE);
            return;
        }

        if(!Utils.isInteger(args[1]) && !args[1].equalsIgnoreCase("all")) {
            sender.sendMessage(Language.INVALID_NUMBER.replace("<argument>", args[1]));
            return;
        }

        int amount = 0;

        if(Utils.isInteger(args[1])) amount = Integer.parseInt(args[1]);

        AtomicBoolean silent = new AtomicBoolean();

        if(args.length == 3 && args[2].toLowerCase().contains("-s")) silent.set(true);

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());
        List<PunishedData> punished = data.getPunished();

        if(args[1].equalsIgnoreCase("all")) amount = punished.size();

        if(amount > punished.size()) {
            sender.sendMessage(Language.PUNISHROLLBACK_INVALID_AMOUNT.replace("<player>", target.getName()));
            return;
        }

        List<PunishedData> toPunish = new ArrayList<>(punished.subList(punished.size() - amount, punished.size()));

        toPunish.forEach(punish -> {
            PunishmentType type = punish.getType();

            if(type != PunishmentType.KICK) {
                Tasks.runSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.getCommand(type) + " " + punish.getName() + " ROLLBACK" + (silent.get() ? " -s" : "")));
            }

            data.getPunished().remove(punish);
        });

        if(!target.isOnline()) data.saveAsync();

        sender.sendMessage(Language.PUNISHROLLBACK_SUCCESS.replace("<amount>", String.valueOf(amount)).replace("<player>", target.getName()));
    }
}
