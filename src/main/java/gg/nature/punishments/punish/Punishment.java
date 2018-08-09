package gg.nature.punishments.punish;

import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.utils.Message;
import gg.nature.punishments.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import gg.nature.punishments.Punishments;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Punishment {

    private PunishmentType type;
    private String sender;
    private String target;
    private long added;
    private String server;
    private String reason;
    private long duration;

    private boolean removed;
    private String removedBy;
    private String removedReason;
    private long removedAt;

    public Punishment(PunishmentType type, String sender, String target, long added, String reason, long duration, boolean request, boolean removed, String server) {
        this.type = type;
        this.sender = sender;
        this.target = target;
        this.added = added;

        this.server = server;

        boolean silent = false;

        if(reason.toLowerCase().contains("-s")) {
            this.reason = reason.replace("-silent", "").replace("-s", "");
            silent = true;
        } else {
            this.reason = reason;
        }

        this.duration = duration;

        this.removed = removed;
        this.removedBy = "";
        this.removedReason = "";
        this.removedAt = 0;

        if(request) this.request(silent);
    }

    public boolean isActive() {
        return !this.removed && (this.duration == Utils.PERMANENT || System.currentTimeMillis() < this.added + this.duration);
    }

    public String getTimeLeft() {
        if(this.removed) return "Removed";
        if(this.duration == Utils.PERMANENT) return "Permanent";
        if(!this.isActive()) return "Expired";

        long time = Math.abs((System.currentTimeMillis() - (this.added + this.duration)));

        return DurationFormatUtils.formatDurationWords(time, true, true);
    }

    private void request(boolean silent) {
        // TODO: bungee implementation
        // Kicking, message

        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(this.target);

        if(this.sender.equalsIgnoreCase("CONSOLE")) {
            Punishments.getInstance().getPunishDataManager().getConsoleData().getPunished().add(offlineTarget.getName() + "/" + type.name() + "-" + this.added);
        } else {
            Player sender = Bukkit.getPlayer(this.sender);
            PunishData senderData = Punishments.getInstance().getPunishDataManager().get(sender.getUniqueId(), sender.getName());

            senderData.getPunished().add(offlineTarget.getName() + "/" + type.name() + "-" + this.added);
        }

        Player target = Bukkit.getPlayer(this.target);

        if(target == null) {

            PunishData targetData = Punishments.getInstance().getPunishDataManager().get(offlineTarget.getUniqueId(), offlineTarget.getName());

            targetData.getPunishments().add(this);
            targetData.saveAsync(false);
        } else {
            PunishData targetData = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

            targetData.getPunishments().add(this);

            switch(type) {
                case MUTE: {
                    target.sendMessage(Utils.translate(Language.MUTED, this, false));
                    break;
                }
                case WARN: {
                    target.sendMessage(Utils.translate(Language.WARNED, this, false));
                    break;
                }
                case BAN: {
                    target.kickPlayer(Utils.translate(Language.BAN_KICK, this, false));
                    break;
                }
                case KICK: {
                    target.kickPlayer(Utils.translate(Language.KICK_KICK, this, false));
                    break;
                }
                case BLACKLIST: {
                    target.kickPlayer(Utils.translate(Language.BLACKLIST_KICK, this, false));
                }
            }
        }

        if(silent) {
            Message.sendMessage(Utils.translate(Language.BROADCAST_SILENT, this, false), "punish.staff");
            return;
        }

        Message.sendMessage(Utils.translate(Language.BROADCAST_PUBLIC, this, false));
    }
}
