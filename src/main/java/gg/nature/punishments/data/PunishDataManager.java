package gg.nature.punishments.data;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Color;
import gg.nature.punishments.utils.Utils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
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

@Getter
public class PunishDataManager implements Listener {

    private Map<UUID, PunishData> dataMap;
    private PunishData consoleData;

    public PunishDataManager() {
        this.dataMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, Punishments.getInstance());
    }

    public void disable() {
        Bukkit.getOnlinePlayers().forEach(player -> this.dataMap.get(player.getUniqueId()).save());

        this.consoleData.save();
    }

    public void loadConsole() {
        this.consoleData = new PunishData(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"), "CONSOLE");

        this.consoleData.load();
    }

    public void loadAlts(String ip, PunishData data) {
        data.getAlts().clear();

        Punishments.getInstance().getDatabaseManager().getPunishments().find(Filters.eq("ip", ip)).forEach((Block) found -> {
            Document document = (Document) found;

            String name = document.getString("name");
            UUID uuid = UUID.fromString(document.getString("uuid"));

            PunishData altData = Punishments.getInstance().getPunishDataManager().get(uuid, name);

            if(altData.getName().equalsIgnoreCase(data.getName())) return;

            if(Utils.getPunishment(altData, PunishmentType.BAN) == null) {
                data.getAlts().add(altData.getName() + "/DEFAULT");
            } else {
                data.getAlts().add(altData.getName() + "/BANNED");
            }
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

    @EventHandler
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

        Utils.kickIfNeeded(data, event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishData data = Punishments.getInstance().getPunishDataManager().get(player.getUniqueId(), player.getName());
        Punishment punishment = Utils.getPunishment(data, PunishmentType.MUTE);

        if(punishment == null) return;

        event.setCancelled(true);
        player.sendMessage(Utils.translate(Language.CHAT_MUTED, punishment, false));
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
