package gg.nature.punishments.file;

import gg.nature.punishments.Punishments;

public class Config {

    public static String DATABASE;
    public static String HOST;
    public static int PORT;
    public static boolean AUTH;
    public static String USER;
    public static char[] PASSWORD;

    public static boolean BUNGEE;
    public static String SERVER_NAME;
    public static boolean IP_BAN;
    public static boolean IP_COMMAND_CONSOLE_ONLY;
    public static boolean SHOW_BANNED_ALTS;

    public Config() {
        ConfigFile config = Punishments.getInstance().getConfigFile();

        DATABASE = config.getString("MONGODB.DATABASE");
        HOST = config.getString("MONGODB.HOST");
        PORT = config.getInt("MONGODB.PORT");
        AUTH = config.getBoolean("MONGODB.AUTH.ENABLED");
        USER = config.getString("MONGODB.AUTH.USER");
        PASSWORD = config.getString("MONGODB.AUTH.PASSWORD").toCharArray();

        BUNGEE = config.getBoolean("BUNGEE");
        SERVER_NAME = config.getString("SERVER_NAME");
        IP_BAN = config.getBoolean("IP_BAN");
        IP_COMMAND_CONSOLE_ONLY = config.getBoolean("IP_COMMAND_CONSOLE_ONLY");
        SHOW_BANNED_ALTS = config.getBoolean("SHOW_BANNED_ALTS");
    }
}
