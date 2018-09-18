package gg.nature.punishments.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.utils.Message;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Collections;

@Getter
public class Mongo {

    private MongoClient client;
    private com.mongodb.client.MongoDatabase database;
    private MongoCollection punishments;

    public Mongo() {
        try {
            if(Punishments.getInstance().getDatabaseManager().isDev()) {
                MongoClientURI uri = new MongoClientURI("mongodb+srv://Nature:Kurac@cluster0-4c7gf.mongodb.net/admin");

                this.client = new MongoClient(uri);
            } else {
                ServerAddress credentials = new ServerAddress(Config.MONGO_HOST, Config.MONGO_PORT);
                MongoCredential credential = MongoCredential.createCredential(Config.MONGO_USER, Config.MONGO_USER, Config.MONGO_PASSWORD);

                this.client = Config.MONGO_AUTH ? new MongoClient(credentials, Collections.singletonList(credential)) : new MongoClient(credentials);
            }

            this.database = this.client.getDatabase(Config.MONGO_DATABASE);
            this.punishments = this.database.getCollection("data");

            Message.sendConsole("&a===================================================");
            Message.sendConsole("&aMONGODB: &aSuccessfully connected to the database.");
            Message.sendConsole("&a===================================================");

            if(!Config.BUNGEE) Punishments.getInstance().getDatabaseManager().setConnected(true);
        } catch (Exception e) {
            Message.sendConsole("&4===================================================");
            Message.sendConsole("&4MONGODB: &cFailed to connect to the database.");
            Message.sendConsole("&4MONGODB: &cPlease check your configuration and try again.");
            Message.sendConsole("&4===================================================");
            Bukkit.shutdown();
        }
    }
}
