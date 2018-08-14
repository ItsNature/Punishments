package gg.nature.punishments.commands.handle;

import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class StaffPunishmentsCommand extends BaseCommand {

    public StaffPunishmentsCommand() {
        super("staffpunishments", Collections.singletonList("staffc"), "punish.staffpunishments", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

            player.openInventory(Punishments.getInstance().getStaffPunishmentsManager().getPunishmentsInventory(data));
            return;
        }

        player.sendMessage(Language.STAFFPUNISHMENTS_USAGE);
    }
}
