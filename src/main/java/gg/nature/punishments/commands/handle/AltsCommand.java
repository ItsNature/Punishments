package gg.nature.punishments.commands.handle;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.utils.Tasks;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class AltsCommand extends BaseCommand {

    public AltsCommand() {
        super("alts", Arrays.asList("altlist", "dupeip"),"punish.alts");

        this.async = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Language.ALTS_USAGE);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

        Punishments.getInstance().getPunishDataManager().loadAlts(data.getIp(), data);

        if(data.getAlts().size() == 0) {
            sender.sendMessage(Language.ALTS_NO_ALTS.replace("<player>", target.getName()));
            return;
        }

        sender.sendMessage(Language.ALTS_ALTS.replace("<player>", target.getName()).replace("<alts>", Utils.getAlts(data)));
    }
}
