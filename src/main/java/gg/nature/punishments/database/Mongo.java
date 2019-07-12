package gg.nature.punishments.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.file.Config;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.utils.Message;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

@Getter
public class Mongo {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> punishments;

    public Mongo() {
        try {
            MongoClientSettings.Builder settings = MongoClientSettings.builder();
            settings.applyConnectionString(getConnectionString());

            if(Config.MONGO_AUTH) {
                settings.credential(MongoCredential.createCredential(Config.MONGO_USER, Config.MONGO_DATABASE, Config.MONGO_PASSWORD));
            }

            this.client = MongoClients.create(settings.build());

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
            Bukkit.getPluginManager().disablePlugin(Punishments.getInstance());
        }
    }

    private ConnectionString getConnectionString() {
        return new ConnectionString("mongodb://" + Config.MONGO_HOST + ":" + Config.MONGO_PORT);
    }

    public void replace(String identifier, Document document) {
        this.punishments.replaceOne(Filters.eq("uuid", identifier), document, new ReplaceOptions().upsert(true));
    }

    public Document find(String identifier) {
        return this.punishments.find(Filters.eq("uuid", identifier)).first();
    }
}
