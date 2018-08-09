package gg.nature.punishments;

import gg.nature.punishments.commands.CommandManager;
import gg.nature.punishments.data.PunishDataManager;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.ConfigFile;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.managers.CheckPunishmentsManager;
import gg.nature.punishments.managers.DatabaseManager;
import gg.nature.punishments.managers.StaffPunishmentsManager;
import gg.nature.punishments.utils.Color;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Punishments extends JavaPlugin {

    @Getter public static Punishments instance;

    private ConfigFile configFile;
    private ConfigFile languageFile;

    private CommandManager commandManager;

    private CheckPunishmentsManager checkPunishmentsManager;
    private DatabaseManager databaseManager;
    private StaffPunishmentsManager staffPunishmentsManager;

    private PunishDataManager punishDataManager;

    @Override
    public void onEnable() {
        instance = this;

        long time = System.currentTimeMillis();

        this.configFile = new ConfigFile("config.yml");
        this.languageFile = new ConfigFile("language.yml");

        new Config();
        new Language();

        this.commandManager = new CommandManager();

        this.checkPunishmentsManager = new CheckPunishmentsManager();
        this.databaseManager = new DatabaseManager();
        this.staffPunishmentsManager = new StaffPunishmentsManager();

        this.punishDataManager = new PunishDataManager();
        this.punishDataManager.loadConsole();

        this.started(time);

        // TODO: reload cmd
    }

    @Override
    public void onDisable() {
        this.commandManager.disable();

        this.punishDataManager.disable();

        this.databaseManager.disable();
    }

    private void started(long time) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        console.sendMessage(Color.translate("&3===&b=============================================&3==="));
        console.sendMessage(Color.translate("- &bName&7: Punishments"));
        console.sendMessage(Color.translate("- &bVersion&7: " + this.getDescription().getVersion()));
        console.sendMessage(Color.translate("- &bAuthor&7: ItsNature"));
        console.sendMessage(Color.translate("- &bEnabled. Took &a" + (System.currentTimeMillis() - time) + " &bms."));
        console.sendMessage(Color.translate("&3===&b=============================================&3==="));
    }
}
