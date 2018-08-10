package gg.nature.punishments.commands;

import gg.nature.punishments.file.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand extends BukkitCommand {

    private boolean forPlayersOnly;

    public BaseCommand(String name) {
        this(name, new ArrayList<>());
    }

    public BaseCommand(String name, List<String> aliases) {
        this(name, aliases, "", false);
    }

    public BaseCommand(String name, String permission) {
        this(name, new ArrayList<>(), permission);
    }

    public BaseCommand(String name, List<String> aliases, String permission) {
        this(name, aliases, permission, false);
    }

    public BaseCommand(String name, boolean forPlayersOnly) {
        this(name, new ArrayList<>(), "", forPlayersOnly);
    }

    public BaseCommand(String name, List<String> aliases, boolean forPlayersOnly) {
        this(name, aliases, "", forPlayersOnly);
    }

    public BaseCommand(String name, String permission, boolean forPlayersOnly) {
        this(name, new ArrayList<>(), permission, forPlayersOnly);
    }

    public BaseCommand(String name, List<String> aliases, String permission, boolean forPlayersOnly) {
        super(name);

        this.setAliases(aliases);
        this.setPermission(permission);
        this.forPlayersOnly = forPlayersOnly;
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if(sender instanceof ConsoleCommandSender && this.forPlayersOnly) {
            sender.sendMessage(Language.FOR_PLAYER_ONLY);
            return true;
        }

        if(!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(Language.NO_PERMISSION);
            return true;
        }

        this.execute(sender, args);
        return true;
    }

    public abstract void execute(CommandSender sender, String[] args);
}