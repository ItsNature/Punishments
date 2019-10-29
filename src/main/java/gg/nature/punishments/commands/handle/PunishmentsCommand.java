package gg.nature.punishments.commands.handle;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.ConfigFile;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.utils.Color;
import org.bukkit.command.CommandSender;

public class PunishmentsCommand extends BaseCommand {

    public PunishmentsCommand() {
        super("punishments");

        this.sync = true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("punish.reload")) {
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                Punishments.getInstance().setConfigFile(new ConfigFile("config.yml"));
                Punishments.getInstance().setLanguageFile(new ConfigFile("language.yml"));

                new Config();
                new Language();

                sender.sendMessage(Language.RELOAD);
                return;
            }

            this.sendMessage(sender);
            return;
        }

        this.sendMessage(sender);
    }

    private void sendMessage(CommandSender sender) {
        sender.sendMessage(Color.translate("&6This server is using punishments plugin created by ItsNature."));
        sender.sendMessage(Color.translate("&6Version: &c" + Punishments.getInstance().getDescription().getVersion()));
    }
}
