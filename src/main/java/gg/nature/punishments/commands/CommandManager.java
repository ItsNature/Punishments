package gg.nature.punishments.commands;

import gg.nature.punishments.commands.handle.CheckPunishmentsCommand;
import gg.nature.punishments.commands.handle.PunishRollbackCommand;
import gg.nature.punishments.commands.handle.ban.UnbanCommand;
import gg.nature.punishments.commands.handle.mute.MuteCommand;
import gg.nature.punishments.commands.handle.mute.UnmuteCommand;
import gg.nature.punishments.commands.handle.StaffPunishmentsCommand;
import gg.nature.punishments.commands.handle.ban.BanCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class CommandManager {

    private CommandMap commandMap;
    private Set<BaseCommand> commands;

    public CommandManager() {
        this.commands = new HashSet<>();

        this.commands.add(new BanCommand());
        this.commands.add(new UnbanCommand());

        this.commands.add(new MuteCommand());
        this.commands.add(new UnmuteCommand());

        this.commands.add(new CheckPunishmentsCommand());
        this.commands.add(new PunishRollbackCommand());
        this.commands.add(new StaffPunishmentsCommand());

        this.commandMap = this.getCommandMap();
        this.commands.forEach(this::registerCommand);
    }

    public void disable() {
        this.commands.clear();
    }

    private CommandMap getCommandMap() {
        try {

            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            return (CommandMap) commandMapField.get(Bukkit.getServer());

        } catch(ReflectiveOperationException e) {
            System.out.println("Could not get command map: " + e.getMessage());
            return null;
        }
    }

    public void registerCommand(BukkitCommand command) {
        this.commandMap.register("punishments", command);
    }
}