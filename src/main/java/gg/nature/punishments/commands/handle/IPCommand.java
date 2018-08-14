package gg.nature.punishments.commands.handle;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IPCommand extends BaseCommand {

    public IPCommand() {
        super("ip", Arrays.asList("checkip", "getip"),"punish.ip");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(Config.IP_COMMAND_CONSOLE_ONLY && sender instanceof Player) {
            sender.sendMessage(Language.NO_PERMISSION);
            return;
        }

        if(args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

            if(data.getIp().equals("")) {
                sender.sendMessage(Language.IP_NOT_FOUND.replace("<player>", target.getName()));
                return;
            }

            sender.sendMessage(Language.IP.replace("<player>", target.getName()).replace("<ip>", data.getIp()));
            return;
        }

        sender.sendMessage(Language.IP_USAGE);
    }
}
