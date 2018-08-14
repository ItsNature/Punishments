package gg.nature.punishments.utils;

import gg.nature.punishments.Punishments;
import org.bukkit.Bukkit;

public class Tasks {

    public static void runAsync(Callable callable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Bukkit.getScheduler().runTaskLater(Punishments.getInstance(), callable::call, delay);
    }

    public interface Callable {
        void call();
    }
}