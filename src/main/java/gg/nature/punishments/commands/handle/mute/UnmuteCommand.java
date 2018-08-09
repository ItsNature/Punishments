package gg.nature.punishments.commands.handle.mute;

import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.commands.BaseCommand;
import gg.nature.punishments.file.Language;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.utils.Message;
import gg.nature.punishments.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public class UnmuteCommand extends BaseCommand {

    public UnmuteCommand() {
        super("unmute", Arrays.asList("unmute", "unsilent"), "punish.unban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Language.UNMUTE_USAGE);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());
        Punishment punishment = Utils.getMutedPunishment(data);

        if(punishment == null) {
            sender.sendMessage(Language.NOT_MUTED.replace("<player>", target.getName()));
            return;
        }

        StringJoiner joiner = new StringJoiner(" ");
        IntStream.range(1, args.length).forEach(i -> joiner.add(args[i]));
        String reason = joiner.toString();

        if(!Utils.isReasonValid(reason)) {
            sender.sendMessage(Language.UNMUTE_USAGE);
            return;
        }

        punishment.setRemoved(true);
        punishment.setRemovedBy(sender.getName());
        punishment.setRemovedReason(Utils.replaceSilent(reason));
        punishment.setRemovedAt(System.currentTimeMillis());

        if(!target.isOnline()) data.saveAsync(false);

        if(Utils.isSilent(reason)) {
            Message.sendMessage(Utils.translate(Language.BROADCAST_SILENT, punishment, true), "punish.staff");
            return;
        }

        Message.sendMessage(Utils.translate(Language.BROADCAST_PUBLIC, punishment, true));
    }
}
