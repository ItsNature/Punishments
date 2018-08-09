package gg.nature.punishments.utils;

import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
    public static long PERMANENT = 2147483647;

    public static String format(long value) {
        return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date(value));
    }

    public static Punishment getByNameAndAdded(PunishData data, String punished) {
        for(Punishment punishment : data.getPunishments()) {
            String name = punished.split("/")[0];
            String firstType = punished.split("/")[1];
            PunishmentType type = PunishmentType.valueOf(firstType.split("-")[0]);
            long added = Long.parseLong(punished.split("-")[1]);

            if(punishment.getTarget().equals(name) && punishment.getType() == type && punishment.getAdded() == added) {
                return punishment;
            }
        }

        return null;
    }

    public static int getAmount(PunishData data, PunishmentType type) {
        int i = 0;

        for(Punishment punishment : data.getPunishments()) {
            if(punishment.getType() == type) {
                i++;
            }
        }

        return i > 64 ? 64 : i == 0 ? 1 : i;
    }

    public static int getSaffAmount(PunishData data, PunishmentType type) {
        int i = 0;

        for(String string : data.getAllPunished()) {
            String firstType = string.split("/")[1];

            if(PunishmentType.valueOf(firstType.split("-")[0]) == type) {
                i++;
            }
        }

        return i > 64 ? 64 : i == 0 ? 1 : i;
    }


    public static void kickIfNeeded(PunishData data, AsyncPlayerPreLoginEvent event) {
        Punishment blacklist = getBlacklistedPunishment(data);

        if(blacklist != null) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Utils.translate(Language.BLACKLIST_KICK, blacklist, false));
            return;
        }

        Punishment ban = getBannedPunishment(data);

        if(ban != null) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Utils.translate(Language.BAN_KICK, ban, false));
        }
    }

    public static Punishment getBlacklistedPunishment(PunishData data) {
        for(Punishment punishment : data.getPunishments()) {
            if(punishment.getType() != PunishmentType.BLACKLIST) continue;
            if(!punishment.isActive()) continue;

            return punishment;
        }

        return null;
    }

    public static Punishment getBannedPunishment(PunishData data) {
        for(Punishment punishment : data.getPunishments()) {
            if(punishment.getType() != PunishmentType.BAN) continue;
            if(!punishment.isActive()) continue;

            return punishment;
        }

        return null;
    }

    public static Punishment getMutedPunishment(PunishData data) {
        for(Punishment punishment : data.getPunishments()) {
            if(punishment.getType() != PunishmentType.MUTE) continue;
            if(!punishment.isActive()) continue;

            return punishment;
        }

        return null;
    }

    public static boolean isSilent(String name) {
        return name.toLowerCase().contains("-s");
    }

    public static String replaceSilent(String reason) {
        if(reason.toLowerCase().contains("-s")) reason = reason.replace("-silent", "").replace("-s", "");

        return reason;
    }

    private static String getPunishment(PunishmentType type, boolean undo) {
        switch(type) {
            case BLACKLIST: return undo ? "unblacklisted" : "blacklisted";
            case BAN: return undo ? "unbanned" : "banned";
            case MUTE: return undo ? "unmuted" : "muted";
            case WARN: return undo ? "unwarned" : "warned";
            case KICK: return "kicked";
        }

        return "";
    }

    public static String getByType(PunishmentType type, boolean undo) {
        switch(type) {
            case BLACKLIST: return undo ? "unblacklist" : "blacklist";
            case BAN: return undo ? "unban" : "ban";
            case MUTE: return undo ? "unmute" : "mute";
            case WARN: return undo ? "unwarn" : "warn";
            case KICK: return "kick";
        }

        return "";
    }

    public static String translate(String message, Punishment punishment, boolean undo) {
        return Color.translate(message
        .replace("<server>", Language.SERVER)
        .replace("<duration>", punishment.getTimeLeft())
        .replace("<date>", format(punishment.getAdded()))
        .replace("<sender>", punishment.getSender())
        .replace("<appeal>", Language.APPEAL)
        .replace("<target>", punishment.getTarget())
        .replace("<type>", getPunishment(PunishmentType.valueOf(punishment.getType().name()), undo))
        .replace("<reason>", punishment.getReason()));
    }

    public static boolean isDurationPermanent(String duration) {
        String dur = duration.toLowerCase();

        return (!dur.startsWith("0") && !dur.startsWith("1")
        && !dur.startsWith("2") && !dur.startsWith("3")
        && !dur.startsWith("4") && !dur.startsWith("5")
        && !dur.startsWith("6") && !dur.startsWith("7")
        && !dur.startsWith("8") && !dur.startsWith("9"))
        || (!dur.endsWith("y") && !dur.endsWith("m")
        && !dur.endsWith("w") && !dur.endsWith("d")
        && !dur.endsWith("h") && !dur.endsWith("s"));
    }

    public static boolean isReasonValid(String reason) {
        return !reason.equals("") && !reason.equalsIgnoreCase("-s");
    }

    public static long parseDuration(String args) {
        return System.currentTimeMillis() - Utils.parseDateDiff(args);
    }

    private static long parseDateDiff(String time) {
        Matcher m = timePattern.matcher(time);

        int hours = 0;
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int minutes = 0;
        int seconds = 0;

        boolean found = false;

        while(m.find()) {
            if(m.group() == null || m.group().isEmpty()) continue;

            for(int c = 0; c < m.groupCount(); ++c) {
                if (m.group(c) == null || m.group(c).isEmpty()) continue;

                found = true;
                break;
            }

            if(!found) continue;

            if(m.group(1) != null && !m.group(1).isEmpty()) years = Integer.parseInt(m.group(1));
            if(m.group(2) != null && !m.group(2).isEmpty()) months = Integer.parseInt(m.group(2));
            if(m.group(3) != null && !m.group(3).isEmpty()) weeks = Integer.parseInt(m.group(3));
            if(m.group(4) != null && !m.group(4).isEmpty()) days = Integer.parseInt(m.group(4));
            if(m.group(5) != null && !m.group(5).isEmpty()) hours = Integer.parseInt(m.group(5));
            if(m.group(6) != null && !m.group(6).isEmpty()) minutes = Integer.parseInt(m.group(6));
            if(m.group(7) == null || m.group(7).isEmpty()) break;

            seconds = Integer.parseInt(m.group(7));
            break;
        }

        if(!found) return PERMANENT;

        GregorianCalendar calendar = new GregorianCalendar();

        if(years > 0) calendar.add(1, years * -1);
        if(months > 0) calendar.add(2, months * -1);
        if(weeks > 0) calendar.add(3, weeks * -1);
        if(days > 0) calendar.add(5, days * -1);
        if(hours > 0) calendar.add(11, hours * -1);
        if(minutes > 0) calendar.add(12, minutes * -1);
        if(seconds > 0) calendar.add(13, seconds * -1);

        GregorianCalendar max = new GregorianCalendar();

        max.add(1, 10);
        return calendar.after(max) ? max.getTimeInMillis() : calendar.getTimeInMillis();
    }

    public static void dispatchCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
