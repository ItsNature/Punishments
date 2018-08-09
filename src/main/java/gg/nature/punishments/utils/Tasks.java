package gg.nature.punishments.utils;

import gg.nature.punishments.Punishments;
import org.bukkit.Bukkit;

public class Tasks {

    public static void run(Callable callable) {
        Bukkit.getScheduler().runTask(Punishments.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Bukkit.getScheduler().runTaskLater(Punishments.getInstance(), callable::call, delay);
    }

    public static void runLaterAsync(Callable callable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Punishments.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimer(Punishments.getInstance(), callable::call, delay, interval);
    }

    public static void runTimerAsync(Callable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Punishments.getInstance(), callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }
}