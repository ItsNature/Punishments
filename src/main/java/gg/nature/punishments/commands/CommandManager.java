package gg.nature.punishments.commands;

import gg.nature.punishments.commands.handle.AltsCommand;
import gg.nature.punishments.commands.handle.CheckPunishmentsCommand;
import gg.nature.punishments.commands.handle.IPCommand;
import gg.nature.punishments.commands.handle.PunishRollbackCommand;
import gg.nature.punishments.commands.handle.PunishmentsCommand;
import gg.nature.punishments.commands.handle.StaffPunishmentsCommand;
import gg.nature.punishments.commands.handle.ban.BanCommand;
import gg.nature.punishments.commands.handle.ban.UnbanCommand;
import gg.nature.punishments.commands.handle.blacklist.BlacklistCommand;
import gg.nature.punishments.commands.handle.blacklist.UnblacklistCommand;
import gg.nature.punishments.commands.handle.kick.KickCommand;
import gg.nature.punishments.commands.handle.mute.MuteCommand;
import gg.nature.punishments.commands.handle.mute.UnmuteCommand;
import gg.nature.punishments.commands.handle.warn.UnwarnCommand;
import gg.nature.punishments.commands.handle.warn.WarnCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandManager {

    private CommandMap commandMap;

    public CommandManager() {
        this.commandMap = this.getCommandMap();

        Arrays.asList(
        new BanCommand(),
        new UnbanCommand(),
        new BlacklistCommand(),
        new UnblacklistCommand(),
        new KickCommand(),
        new MuteCommand(),
        new UnmuteCommand(),
        new WarnCommand(),
        new UnwarnCommand(),
        new AltsCommand(),
        new CheckPunishmentsCommand(),
        new IPCommand(),
        new PunishmentsCommand(),
        new PunishRollbackCommand(),
        new StaffPunishmentsCommand())
        .forEach(this::registerCommand);
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

    private void registerCommand(BukkitCommand command) {
        this.commandMap.register("punishments", command);
    }
}