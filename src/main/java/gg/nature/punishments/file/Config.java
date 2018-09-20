package gg.nature.punishments.file;

import gg.nature.punishments.Punishments;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static String MONGO_DATABASE;
    public static String MONGO_HOST;
    public static int MONGO_PORT;
    public static boolean MONGO_AUTH;
    public static String MONGO_USER;
    public static char[] MONGO_PASSWORD;

    public static String REDIS_HOST;
    public static String REDIS_CHANNEL;
    public static int REDIS_PORT;
    public static boolean REDIS_AUTH;
    public static String REDIS_PASSWORD;

    public static boolean BUNGEE;
    public static String SERVER_NAME;
    public static boolean IP_BAN;
    public static boolean IP_COMMAND_CONSOLE_ONLY;
    public static boolean SHOW_BANNED_ALTS;
    public static List<String> WEIGHT;
    public static int WEIGHT_OP;
    public static int WEIGHT_CONSOLE;

    public Config() {
        ConfigFile config = Punishments.getInstance().getConfigFile();

        MONGO_DATABASE = config.getString("MONGODB.DATABASE");
        MONGO_HOST = config.getString("MONGODB.HOST");
        MONGO_PORT = config.getInt("MONGODB.PORT");
        MONGO_AUTH = config.getBoolean("MONGODB.AUTH.ENABLED");
        MONGO_USER = config.getString("MONGODB.AUTH.USER");
        MONGO_PASSWORD = config.getString("MONGODB.AUTH.PASSWORD").toCharArray();

        REDIS_HOST = config.getString("REDIS.HOST");
        REDIS_CHANNEL = config.getString("REDIS.CHANNEL");
        REDIS_PORT = config.getInt("REDIS.PORT");
        REDIS_AUTH = config.getBoolean("REDIS.AUTH.ENABLED");
        REDIS_PASSWORD = config.getString("REDIS.AUTH.PASSWORD");

        BUNGEE = config.getBoolean("BUNGEE");
        SERVER_NAME = config.getString("SERVER_NAME");
        IP_BAN = config.getBoolean("IP_BAN");
        IP_COMMAND_CONSOLE_ONLY = config.getBoolean("IP_COMMAND_CONSOLE_ONLY");
        SHOW_BANNED_ALTS = config.getBoolean("SHOW_BANNED_ALTS");
        WEIGHT = new ArrayList<>(config.getConfigurationSection("WEIGHT").getKeys(false));
        WEIGHT_OP = config.getInt("DEFAULT.OP");
        WEIGHT_CONSOLE = config.getInt("DEFAULT.CONSOLE");
    }
}
