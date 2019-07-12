package gg.nature.punishments.database;

import gg.nature.punishments.Punishments;
import gg.nature.punishments.data.PunishData;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Message;
import gg.nature.punishments.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Getter
public class Redis {

    private Jedis jedis;
    private JedisPool jedisPool;
    private JedisPubSub jedisSubscriber;

    public Redis() {
        if(!Config.BUNGEE) return;

        try {
            this.jedisPool = new JedisPool(Config.REDIS_HOST, Config.REDIS_PORT);
            this.jedis = new Jedis(Config.REDIS_HOST, Config.REDIS_PORT);

            if(Config.REDIS_AUTH) this.jedis.auth(Config.REDIS_PASSWORD);

            new Thread(() -> this.jedis.subscribe(this.subscribe(), Config.REDIS_CHANNEL)).start();

            Message.sendConsole("&a===================================================");
            Message.sendConsole("&aREDIS: &aSuccessfully connected to the database.");
            Message.sendConsole("&a===================================================");

            Punishments.getInstance().getDatabaseManager().setConnected(true);
            Punishments.getInstance().getDatabaseManager().setRedisConnected(true);
        } catch (Exception e) {
            Message.sendConsole("&4===================================================");
            Message.sendConsole("&4REDIS: &cFailed to connect to the database.");
            Message.sendConsole("&4REDIS: &cPlease check your configuration and try again.");
            Message.sendConsole("&4===================================================");
            Bukkit.getPluginManager().disablePlugin(Punishments.getInstance());
        }
    }

    private JedisPubSub subscribe() {
        this.jedisSubscriber = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if(!channel.equalsIgnoreCase(Config.REDIS_CHANNEL)) return;

                if(message.startsWith("[P]")) {
                    Punishment punishment = Utils.stringToPunishment(message.substring(3));

                    if(punishment != null) punishment.request();
                } else if(message.startsWith("[U]")) {
                    String newMsg = message.substring(3);
                    String[] msg = newMsg.split("<=>");

                    PunishmentType type = PunishmentType.valueOf(msg[0]);
                    OfflinePlayer target = Bukkit.getOfflinePlayer(msg[3]);
                    PunishData data = Punishments.getInstance().getPunishDataManager().get(target.getUniqueId(), target.getName());

                    Punishments.getInstance().getPunishDataManager().unpunish(false, type, target, msg[1], msg[2], data);
                }
            }
        };

        return this.jedisSubscriber;
    }


    public void write(String channel, String message) {
        try(Jedis pool = this.jedisPool.getResource()) {
            if(Config.REDIS_AUTH) pool.auth(Config.REDIS_PASSWORD);

            pool.publish(channel, message);
        }
    }
}
