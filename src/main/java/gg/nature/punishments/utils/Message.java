package gg.nature.punishments.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {

    public static void sendMessage(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendMessage(List<String> messages) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(messages.toArray(new String[0])));
    }

    public static void sendMessageWithout(Player player, String message) {
        Bukkit.getOnlinePlayers().stream().filter(online -> online != player)
        .forEach(online -> online.sendMessage(message));
    }

    public static void sendMessage(String message, String permission) {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(permission))
        .forEach(player -> player.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendMessage(List<String> messages, String permission) {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(permission))
        .forEach(player -> messages.forEach(message -> player.sendMessage(messages.toArray(new String[0]))));
    }

    public static void sendMessageWithoutPlayer(Player player, String message, String permission) {
        Bukkit.getOnlinePlayers().stream().filter(online -> online.hasPermission(permission) &&
        online != player).forEach(online -> online.sendMessage(message));
    }

    public static void sendConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(Color.translate(message));
    }
}