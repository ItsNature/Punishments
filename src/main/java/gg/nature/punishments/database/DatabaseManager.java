package gg.nature.punishments.database;

import gg.nature.punishments.Punishments;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseManager {

	private boolean connected;
	private boolean redisConnected;
	private boolean dev;

	public DatabaseManager() {
		this.connected = false;
		this.redisConnected = false;
		this.dev = true;
	}

	public void disable() {
		if(!this.connected) return;

		Punishments.getInstance().getMongo().getClient().close();

		if(!this.redisConnected) return;

		Punishments.getInstance().getRedis().getJedisSubscriber().unsubscribe();
		Punishments.getInstance().getRedis().getJedis().close();
	}
}