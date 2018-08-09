package gg.nature.punishments.file;

import gg.nature.punishments.Punishments;

public class Language {

    public static String APPEAL;
    public static String SERVER;
    public static String NO_PERMISSION;
    public static String INVALID_NUMBER;

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
    public static String WARN_USAGE;
    public static String UNWARN_USAGE;
    public static String NOT_WARNED;
    public static String WARNED;
    public static String CHAT_MUTED;
    public static String KICK_USAGE;
    public static String IP_USAGE;
    public static String ALTS_USAGE;
    public static String STAFFPUNISHMENTS_USAGE;
    public static String PUNISHMENTS_USAGE;
    public static String PUNISHROLLBACK_USAGE;
    public static String PUNISHROLLBACK_INVALID_AMOUNT;
    public static String PUNISHROLLBACK_SUCCESS;

    public static String BROADCAST_PUBLIC;
    public static String BROADCAST_SILENT;
    public static String BLACKLIST_KICK;
    public static String BAN_KICK;
    public static String KICK_KICK;

    public Language() {
        ConfigFile language = Punishments.getInstance().getLanguageFile();

        APPEAL = language.getString("APPEAL");
        SERVER = language.getString("SERVER");
        NO_PERMISSION = language.getString("NO_PERMISSION");
        INVALID_NUMBER = language.getString("INVALID_NUMBER");

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
        IP_USAGE = language.getString("COMMAND.IP_USAGE");
        ALTS_USAGE = language.getString("COMMAND.ALTS_USAGE");
        STAFFPUNISHMENTS_USAGE = language.getString("COMMAND.STAFFPUNISHMENTS_USAGE");
        PUNISHMENTS_USAGE = language.getString("COMMAND.PUNISHMENTS_USAGE");
        PUNISHROLLBACK_USAGE  = language.getString("COMMAND.PUNISHROLLBACK.USAGE");
        PUNISHROLLBACK_INVALID_AMOUNT = language.getString("COMMAND.PUNISHROLLBACK.INVALID_AMOUNT");
        PUNISHROLLBACK_SUCCESS = language.getString("COMMAND.PUNISHROLLBACK.SUCCESS");

        BROADCAST_PUBLIC = language.getString("PUNISH.BROADCAST_PUBLIC");
        BROADCAST_SILENT = language.getString("PUNISH.BROADCAST_SILENT");
        BLACKLIST_KICK = language.getString("PUNISH.BLACKLIST_KICK");
        BAN_KICK = language.getString("PUNISH.BAN_KICK");
        KICK_KICK = language.getString("PUNISH.KICK_KICK");
    }
}
