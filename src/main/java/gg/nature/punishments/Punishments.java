package gg.nature.punishments;

import gg.nature.punishments.commands.CommandManager;
import gg.nature.punishments.data.PunishDataManager;
import gg.nature.punishments.database.DatabaseManager;
import gg.nature.punishments.database.Mongo;
import gg.nature.punishments.database.Redis;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.ConfigFile;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.managers.CheckPunishmentsManager;
import gg.nature.punishments.managers.StaffPunishmentsManager;
import gg.nature.punishments.utils.Message;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Punishments extends JavaPlugin {

    @Getter public static Punishments instance;

    private ConfigFile configFile;
    private ConfigFile languageFile;

    private CommandManager commandManager;
    private DatabaseManager databaseManager;

    private Mongo mongo;
    private Redis redis;

    private CheckPunishmentsManager checkPunishmentsManager;
    private StaffPunishmentsManager staffPunishmentsManager;

    private PunishDataManager punishDataManager;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();

        instance = this;

        this.loadFiles();

        this.commandManager = new CommandManager();
        this.databaseManager = new DatabaseManager();

        this.mongo = new Mongo();
        this.redis = new Redis();

        this.checkPunishmentsManager = new CheckPunishmentsManager();
        this.staffPunishmentsManager = new StaffPunishmentsManager();

        this.punishDataManager = new PunishDataManager();
        this.punishDataManager.load();

        this.started(time);

        /*
          TODO:
          BANLIST
          FLATFILE
        */
    }

    @Override
    public void onDisable() {
        this.commandManager.disable();
        this.punishDataManager.disable();
        this.databaseManager.disable();
    }

    public void loadFiles() {
        this.configFile = new ConfigFile("config.yml");
        this.languageFile = new ConfigFile("language.yml");

        new Config();
        new Language();
    }

    private void started(long time) {
        Message.sendConsole("&3===&b=============================================&3===");
        Message.sendConsole("- &bName&7: Punishments");
        Message.sendConsole("- &bVersion&7: " + this.getDescription().getVersion());
        Message.sendConsole("- &bAuthor&7: ItsNature");
        Message.sendConsole("- &bEnabled. Took &a" + (System.currentTimeMillis() - time) + " &bms.");
        Message.sendConsole("&3===&b=============================================&3===");
    }
}
