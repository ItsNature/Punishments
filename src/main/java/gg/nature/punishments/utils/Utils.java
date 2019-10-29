package gg.nature.punishments.utils;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.data.AltData;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Utils {

    public static long PERMANENT = Long.MAX_VALUE;
    private static String[] WORDTIMES = { "y", "m", "w", "d", "h", "s" };
    private static String[] TIME = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    public static String format(long value) {
        return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date(value));
    }

    public static int getAmount(PunishData data, PunishmentType type) {
        int i = (int) data.getPunishments().stream().filter(punishment -> punishment.getType() == type).count();

        return i > 64 ? 64 : i == 0 ? 1 : i;
    }

    public static int getSaffAmount(PunishData data, PunishmentType type) {
        int i = (int) data.getPunished().stream().filter(punished -> punished.getType() == type).count();

        return i > 64 ? 64 : i == 0 ? 1 : i;
    }

    public static boolean hasPermission(CommandSender sender, OfflinePlayer target) {
        if(!isPermissible(sender, target)) {
            sender.sendMessage(Language.NO_PERMISSION);
            return false;
        }

        return true;
    }

    private static boolean isPermissible(CommandSender sender, OfflinePlayer target) {
        AtomicInteger weight = new AtomicInteger();
        AtomicBoolean toReturn = new AtomicBoolean();

        Config.WEIGHT.forEach(key -> {
            String permission = Punishments.getInstance().getConfigFile().getString("WEIGHT." + key);

            if (sender instanceof Player) {
                Player player = (Player) sender;
                PunishData senderData = Punishments.getInstance().getPunishDataManager().get(player.getUniqueId(), player.getName());

                if (player.hasPermission(permission)) senderData.setWeight(player.isOp() ? Config.WEIGHT_OP : Integer.valueOf(key));

                weight.set(senderData.getWeight());
            } else {
                weight.set(Config.WEIGHT_CONSOLE);
            }

            PunishData targetData = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

            if (target.isOnline()) {
                Player online = (Player) target;

                if (online.hasPermission(permission)) targetData.setWeight(Integer.valueOf(key));
            }

            if (target.isOp()) targetData.setWeight(Config.WEIGHT_OP);
            if (weight.get() >= targetData.getWeight()) toReturn.set(true);
        });

        return toReturn.get();
    }

    public static void check(PunishData data, AsyncPlayerPreLoginEvent event) {
        Punishment blacklist = getPunishment(data, PunishmentType.BLACKLIST);

        if (blacklist != null) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Utils.translate(Language.BLACKLIST_KICK, blacklist, false));
            return;
        }

        Punishment ban = getPunishment(data, PunishmentType.BAN);

        if (ban != null) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Utils.translate(Language.BAN_KICK, ban, false));
            return;
        }

        boolean bannedAlts = false;
        String name = "";

        AltData altData = data.getAlts().stream().filter(alt -> alt.getType().equals("BAN")).findFirst().orElse(null);

        if(altData != null) {
            bannedAlts = true;
            name = altData.getName();
        }

        if (!bannedAlts) return;

        if (Config.IP_BAN) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Language.IP_CONNECTED.replace("<server>", Language.SERVER).replace("<player>", name));
            return;
        }

        if (Config.SHOW_BANNED_ALTS) {
            Language.ALTS_ALTS_BANNED.forEach(message -> Message.sendMessage(message
            .replace("<player>", data.getName()).replace("<alts>", getAlts(data)), "punish.broadcast.alts"));
        }
    }

    public static Punishment getPunishment(OfflinePlayer target, PunishmentType type) {
        PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

        return getPunishment(data, type);
    }

    public static Punishment getPunishment(PunishData data, PunishmentType type) {
        return data.getPunishments().stream().filter(punishment ->
        punishment.getType() == type && punishment.isActive()).findFirst().orElse(null);
    }

    public static Punishment getByNameAndAdded(PunishData data, PunishmentType type, long added) {
        return data.getPunishments().stream().filter(punishment ->
        punishment.getType() == type && punishment.getAdded() == added).findFirst().orElse(null);
    }

    public static Punishment getLastWarnPunishment(PunishData data) {
        return data.getPunishments().stream().filter(punishment -> punishment.getType() == PunishmentType.WARN &&
        punishment.isActive()).max(Comparator.comparing(Punishment::getAdded)).orElse(null);
    }

    public static String getAlts(PunishData data) {
        StringJoiner joiner = new StringJoiner(Language.ALTS_FORMAT);

        data.getAlts().forEach(alt ->
        joiner.add(Bukkit.getOfflinePlayer(
        alt.getName()).isOnline() ? Language.ALTS_ONLINE + alt.getName() :
        alt.getType().equals("BAN") ? Language.ALTS_BANNED + alt.getType() :
        Language.ALTS_OFFLINE + alt.getName()));

        return joiner.toString();
    }

    public static boolean isSilent(String name) {
        return name.toLowerCase().contains("-s");
    }

    public static String replaceSilent(String reason) {
        return reason.replace("-silent", "").replace("-s", "");
    }

    private static String getPunishment(PunishmentType type, boolean undo) {
        String toReturn = "";

        switch (type) {
            case BLACKLIST: toReturn = (undo ? "unblacklisted" : "blacklisted"); break;
            case BAN: toReturn = undo ? "unbanned" : "banned"; break;
            case MUTE: toReturn = undo ? "unmuted" : "muted"; break;
            case WARN: toReturn = undo ? "unwarned" : "warned"; break;
            case KICK: toReturn = "kicked";
        }

        return toReturn;
    }

    public static String translate(String message, Punishment punishment, boolean undo) {
        return message.replace("<server>", Language.SERVER)
        .replace("<duration>", punishment.getTimeLeft())
        .replace("<date>", format(punishment.getAdded()))
        .replace("<sender>", undo ? "<sender>" : punishment.getSender())
        .replace("<appeal>", Language.APPEAL)
        .replace("<target>", punishment.getTarget())
        .replace("<type>", getPunishment(PunishmentType.valueOf(punishment.getType().name()), undo))
        .replace("<reason>", punishment.getReason());
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isReasonValid(String reason) {
        return !reason.equals("") && !reason.equalsIgnoreCase("-s") && !reason.contains("<=>");
    }

    public static String punishmentToString(Punishment punishment, boolean request) {
        return punishment.getType().name() + "," + punishment.getSender() + "," +
        punishment.getTarget() + "," + String.valueOf(punishment.getAdded()) + "," +
        punishment.getReason() + "," + String.valueOf(punishment.getDuration()) + "," +
        request + "," + punishment.isRemoved() + "," +
        punishment.getServer() + "," + punishment.getRemovedBy() + "," +
        punishment.getRemovedReason() + "," + String.valueOf(punishment.getRemovedAt());
    }

    public static Punishment stringToPunishment(String string) {
        if (string.isEmpty()) return null;

        String[] args = string.split(",");

        String type = args[0];
        String added = args[3];
        String duration = args[5];
        String req = args[6];
        String removed = args[7];
        String removedAt = args[11];

        Punishment toReturn = new Punishment(PunishmentType.valueOf(type), args[1], args[2], Long.valueOf(added), args[4], Long.valueOf(duration), Boolean.valueOf(req), Boolean.valueOf(removed), args[8]);

        toReturn.setRemovedBy(args[9]);
        toReturn.setRemovedReason(args[10]);
        toReturn.setRemovedAt(Long.valueOf(removedAt));

        return toReturn;
    }

    public static long getDuration(String duration) {
        return Stream.of(TIME).anyMatch(duration::startsWith) && Stream.of(WORDTIMES).anyMatch(duration::contains) ? parseDuration(duration) : PERMANENT;
    }

    private static long parseDuration(String time) {
        String fixed = time.toLowerCase().trim();
        String[] array = fixed.split("(?<=[a-zA-Z])(?=[0-9])");
        Calendar calendar = Calendar.getInstance();

        Stream.of(array).forEach(each -> {
            String[] split = each.split("(?=[a-zA-Z])");
            String addTo = each.substring(split[0].length());

            calendar.add(getTimeByString(addTo), Integer.valueOf(split[0]));
        });

        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

    private static int getTimeByString(String time) {
        return time.startsWith("y") ? Calendar.YEAR : time.startsWith("mo") ? Calendar.MONTH :
        time.startsWith("w") ? Calendar.WEEK_OF_YEAR : time.startsWith("d") ? Calendar.DAY_OF_YEAR :
        time.startsWith("h") ? Calendar.HOUR : time.startsWith("m") ? Calendar.MINUTE :
        time.startsWith("s") ? Calendar.SECOND : 0;
    }
}
