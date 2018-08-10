package gg.nature.punishments.file;

import gg.nature.punishments.Punishments;

import java.util.List;

public class Language {

    public static String APPEAL;
    public static String SERVER;
    public static String NO_PERMISSION;
    public static String FOR_PLAYER_ONLY;
    public static String INVALID_NUMBER;
    public static String PLAYER_NOT_ONLINE;
    public static String DATABASE_NOT_CONNECTED;
    public static String INVALID_NAME;

    public static String BAN_USAGE;
    public static String UNBAN_USAGE;
    public static String ALREADY_BANNED;
    public static String NOT_BANNED;

    public static String BLACKLIST_USAGE;
    public static String UNBLACKLIST_USAGE;
    public static String ALREADY_BLACKLISTED;
    public static String NOT_BLACKLISTED;

    public static String MUTE_USAGE;
    public static String UNMUTE_USAGE;
    public static String ALREADY_MUTED;
    public static String NOT_MUTED;
    public static String MUTED;
    public static String CHAT_MUTED;

    public static String WARN_USAGE;
    public static String UNWARN_USAGE;
    public static String NOT_WARNED;
    public static String WARNED;

    public static String KICK_USAGE;

    public static String IP_USAGE;
    public static String IP_NOT_FOUND;
    public static String IP;

    public static String ALTS_USAGE;
    public static String ALTS_FORMAT;
    public static String ALTS_ONLINE;
    public static String ALTS_OFFLINE;
    public static String ALTS_BANNED;
    public static String ALTS_NO_ALTS;
    public static String ALTS_ALTS;
    public static List<String> ALTS_ALTS_BANNED;

    public static String STAFFPUNISHMENTS_USAGE;
    public static String CHECKPUNISHMENTS_USAGE;

    public static String PUNISHROLLBACK_USAGE;
    public static String PUNISHROLLBACK_INVALID_AMOUNT;
    public static String PUNISHROLLBACK_SUCCESS;

    public static String RELOAD;

    public static String BROADCAST_PUBLIC;
    public static String BROADCAST_SILENT;
    public static String BLACKLIST_KICK;
    public static String BAN_KICK;
    public static String KICK_KICK;
    public static String IP_CONNECTED;

    public Language() {
        ConfigFile language = Punishments.getInstance().getLanguageFile();

        APPEAL = language.getString("APPEAL");
        SERVER = language.getString("SERVER");
        NO_PERMISSION = language.getString("NO_PERMISSION");
        FOR_PLAYER_ONLY = language.getString("FOR_PLAYER_ONLY");
        INVALID_NUMBER = language.getString("INVALID_NUMBER");
        PLAYER_NOT_ONLINE = language.getString("PLAYER_NOT_ONLINE");
        DATABASE_NOT_CONNECTED = language.getString("DATABASE_NOT_CONNECTED");
        INVALID_NAME = language.getString("INVALID_NAME");

        BAN_USAGE = language.getString("COMMAND.BAN.BAN_USAGE");
        UNBAN_USAGE = language.getString("COMMAND.BAN.UNBAN_USAGE");
        ALREADY_BANNED = language.getString("COMMAND.BAN.ALREADY_BANNED");
        NOT_BANNED = language.getString("COMMAND.BAN.NOT_BANNED");

        BLACKLIST_USAGE = language.getString("COMMAND.BLACKLIST.BLACKLIST_USAGE");
        UNBLACKLIST_USAGE = language.getString("COMMAND.BLACKLIST.UNBLACKLIST_USAGE");
        ALREADY_BLACKLISTED = language.getString("COMMAND.BLACKLIST.ALREADY_BLACKLISTED");
        NOT_BLACKLISTED = language.getString("COMMAND.BLACKLIST.NOT_BLACKLISTED");

        MUTE_USAGE = language.getString("COMMAND.MUTE.MUTE_USAGE");
        UNMUTE_USAGE = language.getString("COMMAND.MUTE.UNMUTE_USAGE");
        ALREADY_MUTED = language.getString("COMMAND.MUTE.ALREADY_MUTED");
        NOT_MUTED = language.getString("COMMAND.MUTE.NOT_MUTED");
        MUTED = language.getString("COMMAND.MUTE.MUTED");
        CHAT_MUTED = language.getString("COMMAND.MUTE.CHAT_MUTED");

        WARN_USAGE = language.getString("COMMAND.WARN.WARN_USAGE");
        UNWARN_USAGE = language.getString("COMMAND.WARN.UNWARN_USAGE");
        NOT_WARNED = language.getString("COMMAND.WARN.NOT_WARNED");
        WARNED = language.getString("COMMAND.WARN.WARNED");

        KICK_USAGE = language.getString("COMMAND.KICK_USAGE");

        IP_USAGE = language.getString("COMMAND.IP.USAGE");
        IP_NOT_FOUND = language.getString("COMMAND.IP.NOT_FOUND");
        IP = language.getString("COMMAND.IP.IP");

        ALTS_USAGE = language.getString("COMMAND.ALTS.USAGE");
        ALTS_FORMAT = language.getString("COMMAND.ALTS.FORMAT");
        ALTS_ONLINE = language.getString("COMMAND.ALTS.ONLINE");
        ALTS_OFFLINE = language.getString("COMMAND.ALTS.OFFLINE");
        ALTS_BANNED = language.getString("COMMAND.ALTS.BANNED");
        ALTS_NO_ALTS = language.getString("COMMAND.ALTS.NO_ALTS");
        ALTS_ALTS = language.getString("COMMAND.ALTS.ALTS");
        ALTS_ALTS_BANNED = language.getStringList("COMMAND.ALTS.ALTS_BANNED");

        STAFFPUNISHMENTS_USAGE = language.getString("COMMAND.STAFFPUNISHMENTS_USAGE");
        CHECKPUNISHMENTS_USAGE = language.getString("COMMAND.CHECKPUNISHMENTS_USAGE");

        PUNISHROLLBACK_USAGE  = language.getString("COMMAND.PUNISHROLLBACK.USAGE");
        PUNISHROLLBACK_INVALID_AMOUNT = language.getString("COMMAND.PUNISHROLLBACK.INVALID_AMOUNT");
        PUNISHROLLBACK_SUCCESS = language.getString("COMMAND.PUNISHROLLBACK.SUCCESS");

        RELOAD = language.getString("COMMAND.RELOAD");

        BROADCAST_PUBLIC = language.getString("PUNISH.BROADCAST_PUBLIC");
        BROADCAST_SILENT = language.getString("PUNISH.BROADCAST_SILENT");
        BLACKLIST_KICK = language.getString("PUNISH.BLACKLIST_KICK");
        BAN_KICK = language.getString("PUNISH.BAN_KICK");
        KICK_KICK = language.getString("PUNISH.KICK_KICK");
        IP_CONNECTED = language.getString("PUNISH.IP_CONNECTED");
    }
}
