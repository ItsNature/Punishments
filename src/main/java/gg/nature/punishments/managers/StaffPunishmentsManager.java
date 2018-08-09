package gg.nature.punishments.managers;

import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.ItemBuilder;
import gg.nature.punishments.utils.Utils;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class StaffPunishmentsManager implements Listener {

    private ItemStack glass;

    public StaffPunishmentsManager() {
        this.glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 7).build();

        Bukkit.getPluginManager().registerEvents(this, Punishments.getInstance());
    }

    public Inventory getPunishmentsInventory(PunishData data) {
        Inventory inventory = Bukkit.createInventory(null, 45, Color.translate("&6[S] " + data.getName()));

        inventory.setItem(11, new ItemBuilder(Material.WOOL, Utils.getSaffAmount(data,
        PunishmentType.BLACKLIST), 15).setName("&8Blacklists").build());

        inventory.setItem(13, new ItemBuilder(Material.WOOL, Utils.getSaffAmount(data,
        PunishmentType.BAN), 14).setName("&cBans").build());

        inventory.setItem(15, new ItemBuilder(Material.WOOL, Utils.getSaffAmount(data,
        PunishmentType.MUTE), 1).setName("&6Mutes").build());

        inventory.setItem(30, new ItemBuilder(Material.WOOL, Utils.getSaffAmount(data,
        PunishmentType.WARN), 13).setName("&aWarns").build());

        inventory.setItem(32, new ItemBuilder(Material.WOOL, Utils.getSaffAmount(data,
        PunishmentType.KICK), 9).setName("&3Kicks").build());

        IntStream.range(0, inventory.getSize()).filter(i -> inventory.getItem(i) == null)
        .forEach(i -> inventory.setItem(i, glass));

        return inventory;
    }

    private Inventory getPunishmentsByType(PunishData data, PunishmentType type, int page) {
        List<String> punished = data.getAllPunished();

        int maxPages = (int) Math.ceil(punished.size() / 18.0D);

        Inventory inventory = Bukkit.createInventory(null, 54, Color.translate("&6[S] " + data.getName() + " - " + page + "/" + (maxPages == 0 ? 1 : maxPages)));

        inventory.setItem(0, new ItemBuilder(Material.CARPET, 1, 7).setName("&ePrevious Page").build());
        inventory.setItem(4, new ItemBuilder(Material.PAPER).setName("&ePage: &e" + page + "&6/&e" + (maxPages == 0 ? 1 : maxPages)).build());
        inventory.setItem(8, new ItemBuilder(Material.CARPET, 1, 7).setName("&eNext Page").build());

        IntStream.rangeClosed(9, 17).forEach(i -> inventory.setItem(i, glass));

        punished.forEach(punish -> {
            if(punished.indexOf(punish) < page * 18 - 18) return;
            if(punished.indexOf(punish) >= page * 18) return;

            String firstType = punish.split("/")[1];

            if(PunishmentType.valueOf(firstType.split("-")[0]) != type) return;

            Punishment punishment = Utils.getByNameAndAdded(data, punish);

            if(punishment == null) return;

            ItemStack item;
            List<String> lore = new ArrayList<>();

            if(punishment.isActive()) {
                lore.add(Color.translate("&7&m--------------------------------"));
                lore.add(Color.translate("&7UUID: &c" + Bukkit.getOfflinePlayer(punishment.getTarget()).getUniqueId()));
                lore.add(Color.translate("&7Reason: &c" + punishment.getReason()));
                lore.add(Color.translate("&7Duration: &c" + punishment.getTimeLeft()));
                lore.add(Color.translate("&7Server: &c" + punishment.getServer()));
                lore.add(Color.translate("&7&m--------------------------------"));

                item = new ItemBuilder(Material.WOOL, 1, 5).setName("&e" + punishment.getTarget()).setLore(lore).build();
            } else if(punishment.isRemoved()){
                lore.add(Color.translate("&7&m--------------------------------"));
                lore.add(Color.translate("&7UUID: &c" + Bukkit.getOfflinePlayer(punishment.getTarget()).getUniqueId()));
                lore.add(Color.translate("&7Reason: &c" + punishment.getReason()));
                lore.add(Color.translate("&7Server: &c" + punishment.getServer()));
                lore.add(Color.translate("&7Removed By: &c" + punishment.getRemovedBy()));
                lore.add(Color.translate("&7Removed Reason: &c" + punishment.getRemovedReason()));
                lore.add(Color.translate("&7Removed At: &c" + Utils.format(punishment.getRemovedAt())));
                lore.add(Color.translate("&7&m--------------------------------"));

                item = new ItemBuilder(Material.WOOL, 1, 14).setName("&e" + punishment.getTarget()).setLore(lore).build();
            } else {
                lore.add(Color.translate("&7&m--------------------------------"));
                lore.add(Color.translate("&7UUID: &c" + Bukkit.getOfflinePlayer(punishment.getTarget()).getUniqueId()));
                lore.add(Color.translate("&7Reason: &c" + punishment.getReason()));
                lore.add(Color.translate("&7Epired At: &c" + Utils.format(punishment.getAdded() + punishment.getDuration())));
                lore.add(Color.translate("&7Server: &c" + punishment.getServer()));
                lore.add(Color.translate("&7&m--------------------------------"));

                item = new ItemBuilder(Material.WOOL, 1, 14).setName("&e" + punishment.getTarget()).setLore(lore).build();
            }

            inventory.setItem(18 + punished.indexOf(punish) % 18, item);
        });

        IntStream.rangeClosed(36, 44).forEach(i -> inventory.setItem(i, glass));

        inventory.setItem(49, new ItemBuilder(Material.CARPET).setName("&eType:&6 " + type.name()).build());
        inventory.setItem(53, new ItemBuilder(Material.REDSTONE_BLOCK).setName("&6Go back").build());

        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inv = event.getInventory().getName();

        if(!inv.contains("[S]")) return;

        Player player = (Player) event.getWhoClicked();

        if(!player.hasPermission("punish.staffpunishments")) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();

        if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(inv.substring(6).split(" -")[0]);
        String name = item.getItemMeta().getDisplayName();

        switch(item.getType()) {
            case WOOL: {
                if(item.getDurability() == 5) return;

                PunishmentType type = PunishmentType.valueOf(name.substring(2, name.length() - 1).toUpperCase());
                PunishData data = Punishments.getInstance().getPunishDataManager().get(offlinePlayer.getUniqueId(), offlinePlayer.getName());

                player.openInventory(this.getPunishmentsByType(data, type, 1));
                break;
            }
            case REDSTONE_BLOCK: {
                PunishData data = Punishments.getInstance().getPunishDataManager().get(offlinePlayer.getUniqueId(), offlinePlayer.getName());

                player.openInventory(this.getPunishmentsInventory(data));
                break;
            }
            case CARPET: {
                PunishmentType type = PunishmentType.valueOf(event.getInventory().getItem(49).getItemMeta().getDisplayName().split(" ")[1]);

                if(!name.contains("Page")) return;

                String number = inv.split("- ")[1];

                int page = Integer.parseInt(number.split("/")[0]);
                int total = Integer.parseInt(number.split("/")[1]);

                if(name.contains("Previous")) {
                    if (page == 1) {
                        player.sendMessage(Color.translate("&eYou're already on the first page."));
                        return;
                    }

                    PunishData data = Punishments.getInstance().getPunishDataManager().get(offlinePlayer.getUniqueId(), offlinePlayer.getName());

                    player.openInventory(this.getPunishmentsByType(data, type, page - 1));
                    return;
                }

                if(!name.contains("Next")) return;

                if(page + 1 > total) {
                    player.sendMessage(Color.translate("&eThere are no more pages."));
                    return;
                }

                PunishData data = Punishments.getInstance().getPunishDataManager().get(offlinePlayer.getUniqueId(), offlinePlayer.getName());

                player.openInventory(this.getPunishmentsByType(data, type, page + 1));
            }
        }
    }
}
