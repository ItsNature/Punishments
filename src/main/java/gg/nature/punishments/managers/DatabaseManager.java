package gg.nature.punishments.managers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import gg.nature.punishments.utils.Message;
import gg.nature.punishments.utils.Tasks;
import lombok.Getter;
import gg.nature.punishments.file.Config;

import java.util.Collections;

@Getter
public class DatabaseManager {

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection punishments;

	private boolean connected;
	private boolean dev;

	public DatabaseManager() {
		this.connected = false;
		this.dev = true;

		try {
			if(dev) {
				MongoClientURI uri = new MongoClientURI("mongodb+srv://Nature:Kurac@cluster0-4c7gf.mongodb.net/admin");

				this.client = new MongoClient(uri);
			} else {
				ServerAddress credentials = new ServerAddress(Config.HOST, Config.PORT);

				if(Config.AUTH) {
					MongoCredential credential = MongoCredential.createCredential(Config.USER, Config.USER, Config.PASSWORD);
					this.client = new MongoClient(credentials, Collections.singletonList(credential));
				} else {
					this.client = new MongoClient(credentials);
				}
			}

			this.database = this.client.getDatabase(Config.DATABASE);

			this.punishments = this.database.getCollection("data");

			Tasks.run(() -> {
				this.connected = true;
				Message.sendConsole("&a&lConnected to mongo database.");
			});
		} catch (Exception e) {
			Message.sendConsole("&4&lCouldn't connect to mongo database.");
		}
	}

	public void disable() {
		this.client.close();
	}
}