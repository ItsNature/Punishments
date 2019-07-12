package gg.nature.punishments.data;

import com.mongodb.client.model.Filters;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Message;
import gg.nature.punishments.utils.Tasks;
import gg.nature.punishments.utils.Utils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class PunishDataManager implements Listener {

    private Map<UUID, PunishData> dataMap;
    private boolean loaded;
    private PunishData consoleData;

    public PunishDataManager() {
        this.dataMap = new HashMap<>();
        this.loaded = false;

        Bukkit.getPluginManager().registerEvents(this, Punishments.getInstance());
    }

    public void disable() {
        if(!this.loaded) return;

        Bukkit.getOnlinePlayers().forEach(player -> this.dataMap.get(player.getUniqueId()).save());

        this.consoleData.save();
    }

    public void load() {
        Bukkit.getOnlinePlayers().forEach(player -> this.get(player.getUniqueId(), player.getName()));

        this.consoleData = new PunishData(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"), "CONSOLE");
        this.consoleData.load();
        this.consoleData.setWeight(Config.WEIGHT_CONSOLE);

        this.loaded = true;
    }

    public void unpunish(boolean bungee, PunishmentType type, OfflinePlayer target, String sender, String reason, PunishData data) {
        Punishment punishment = type == PunishmentType.WARN ? Utils.getLastWarnPunishment(data) : Utils.getPunishment(data, type);

        if(bungee) {
            Punishments.getInstance().getRedis().write(Config.REDIS_CHANNEL, "[U]" + type.name() + "<=>" + sender + "<=>" + reason + "<=>" + target.getName());
            return;
        }

        if(punishment == null) return;

        boolean silent = Utils.isSilent(reason);
        reason = Utils.replaceSilent(reason);

        punishment.setRemoved(true);
        punishment.setRemovedBy(sender);
        punishment.setRemovedReason(reason);

        PunishData targetData = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

        if(!target.isOnline()) targetData.saveAsync();

        if(silent) {
            Message.sendMessage(Utils.translate(Language.BROADCAST_SILENT, punishment, true).replace("<sender>", sender), "punish.broadcast.unpunish.silent");
            return;
        }

        Message.sendMessage(Utils.translate(Language.BROADCAST_PUBLIC, punishment, true).replace("<sender>", sender));
    }

    public void loadAlts(String ip, PunishData data) {
        if(ip.equals("")) return;

        data.getAlts().clear();

        Punishments.getInstance().getMongo().getPunishments().find(Filters.eq("ip", ip)).forEach((Consumer<? super Document>) document -> {
            String name = document.getString("name");
            UUID uuid = UUID.fromString(document.getString("uuid"));

            PunishData altData = Punishments.getInstance().getPunishDataManager().get(uuid, name);

            if(altData.getName().equalsIgnoreCase(data.getName())) return;

            boolean exists = Utils.getPunishment(altData, PunishmentType.BAN) == null;

            data.getAlts().add(new AltData(altData.getName(), exists ? "NONE" : "BAN"));
        });
    }

    public PunishData get(UUID uuid, String name) {
        PunishData data = this.dataMap.get(uuid);

        if(data == null) {
            data = new PunishData(uuid, name);

            data.load();
        }

        return data;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!Punishments.getInstance().getDatabaseManager().isConnected()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Language.DATABASE_NOT_CONNECTED);
            return;
        }

        if(event.getName().equalsIgnoreCase("CONSOLE")) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Language.INVALID_NAME);
            return;
        }

        PunishData data = this.get(event.getUniqueId(), event.getName());

        if(!data.isLoaded()) data.load();

        this.loadAlts(event.getAddress().getHostName(), data);

        Utils.check(data, event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Punishment punishment = Utils.getPunishment(event.getPlayer(), PunishmentType.MUTE);
        if(punishment == null) return;

        event.setCancelled(true);
        event.getPlayer().sendMessage(Utils.translate(Language.CHAT_MUTED, punishment, false));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!Punishments.getInstance().getDatabaseManager().isConnected()) return;

        Player player = event.getPlayer();
        PunishData data = this.get(player.getUniqueId(), player.getName());

        data.setIp(player.getAddress().getHostName());

        if(data.isLoaded()) data.saveAsync();
    }
}
