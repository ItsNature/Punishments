package gg.nature.punishments.utils;

import org.bukkit.Bukkit;

public class Message {

    public static void sendMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendMessage(String message, String permission) {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(permission))
        .forEach(player -> player.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(Color.translate(message));
    }
}