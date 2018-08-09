package gg.nature.punishments.data;

import lombok.Getter;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.utils.Color;
import gg.nature.punishments.utils.Utils;
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
        // TODO: save all data

        this.consoleData.save(true);
    }

    public void loadConsole() {
        this.consoleData = new PunishData(UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"), "CONSOLE");

        this.consoleData.load();
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!Punishments.getInstance().getDatabaseManager().isConnected()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cFailed to load data: Database not connected!"));
            return;
        }

        if(event.getName().equalsIgnoreCase("CONSOLE")) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cYou cannot join with that name!"));
            return;
        }

        PunishData data = this.get(event.getUniqueId(), event.getName());

        if(!data.isLoaded()) data.load();

        Utils.kickIfNeeded(data, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishData data = Punishments.getInstance().getPunishDataManager().get(player.getUniqueId(), player.getName());
        Punishment punishment = Utils.getMutedPunishment(data);

        if(punishment == null) return;

        event.setCancelled(true);
        player.sendMessage(Utils.translate(Language.CHAT_MUTED, punishment, false));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PunishData data = this.get(player.getUniqueId(), player.getName());

        data.setIp(player.getAddress().getHostName());

        if(data.isLoaded()) data.saveAsync(true);
    }

    public PunishData get(UUID uuid, String name) {
        PunishData data = this.dataMap.get(uuid);

        if(data == null) {
            data = new PunishData(uuid, name);

            data.load();
        }

        return data;
    }
}
