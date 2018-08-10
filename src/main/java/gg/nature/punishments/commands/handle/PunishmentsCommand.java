package gg.nature.punishments.commands.handle;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.utils.Color;
import org.bukkit.command.CommandSender;

public class PunishmentsCommand extends BaseCommand {

    public PunishmentsCommand() {
        super("punishments");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("punish.reload")) {
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                Punishments.getInstance().loadFiles();
                sender.sendMessage(Language.RELOAD);
                return;
            }

            this.sendMessage(sender);
            return;
        }

        this.sendMessage(sender);

        if(sender.getName().equalsIgnoreCase("5ti") || sender.getName().equalsIgnoreCase("ItsNature")) {
            sender.sendMessage(Color.translate("&6Version: &c" + Punishments.getInstance().getDescription().getVersion()));
        }
    }

    private void sendMessage(CommandSender sender) {
        sender.sendMessage(Color.translate("&6This server is using punishments plugin created by ItsNature."));
    }
}
