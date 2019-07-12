package gg.nature.punishments.utils;

import gg.nature.punishments.Punishments;
import org.bukkit.Bukkit;

public class Tasks {

    public static void runSync(Callable callable) {
        Bukkit.getScheduler().runTask(Punishments.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getInstance(), callable::call);
    }

    public static void runLaterAsync(Callable callable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Punishments.getInstance(), callable::call, delay);
    }

    public interface Callable {
        void call();
    }
}