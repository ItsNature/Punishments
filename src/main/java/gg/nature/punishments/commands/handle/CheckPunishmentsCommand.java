package gg.nature.punishments.commands.handle;

import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CheckPunishmentsCommand extends BaseCommand {

    public CheckPunishmentsCommand() {
        super("checkpunishments", Arrays.asList("punishments", "c"), "punish.checkpunishments", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

            player.openInventory(Punishments.getInstance().getCheckPunishmentsManager().getPunishmentsInventory(data));
            return;
        }

        player.sendMessage(Language.PUNISHMENTS_USAGE);
    }
}
