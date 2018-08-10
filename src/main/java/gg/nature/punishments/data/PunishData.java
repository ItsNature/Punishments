package gg.nature.punishments.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class PunishData {

    private UUID uuid;
    private String name;

    private String ip;
    private List<String> punished;
    private List<String> alts;
    private Set<Punishment> punishments;

    private boolean loaded;

    public PunishData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.ip = "";
        this.punished = new ArrayList<>();
        this.alts = new ArrayList<>();
        this.punishments = new HashSet<>();

        this.loaded = false;
    }

    public void load() {
        Document uuidDocument = (Document) Punishments.getInstance().getDatabaseManager().getPunishments()
        .find(Filters.eq("uuid", this.uuid.toString())).first();

        if(uuidDocument != null) {
            this.ip = uuidDocument.getString("ip");

            for(JsonElement doc : new JsonParser().parse(uuidDocument.getString("punished")).getAsJsonArray()) {
                JsonObject jsonDoc = doc.getAsJsonObject();

                String name = jsonDoc.get("name").getAsString();
                String type = jsonDoc.get("type").getAsString();
                long added = jsonDoc.get("added").getAsLong();

                String punished = name + "/" + type + "-" + added;

                this.punished.add(punished);
            }

            for(JsonElement doc : new JsonParser().parse(uuidDocument.getString("punishments")).getAsJsonArray()) {
                JsonObject jsonDoc = doc.getAsJsonObject();

                PunishmentType type = PunishmentType.valueOf(jsonDoc.get("type").getAsString());
                String addedBy = jsonDoc.get("addedBy").getAsString();
                String addedTo = jsonDoc.get("addedTo").getAsString();
                long addedAt = jsonDoc.get("addedAt").getAsLong();
                String reason = jsonDoc.get("reason").getAsString();
                long duration = jsonDoc.get("duration").getAsLong();
                boolean removed = jsonDoc.get("removed").getAsBoolean();
                String server = jsonDoc.get("server").getAsString();

                Punishment punishment = new Punishment(type, addedBy, addedTo, addedAt, reason, duration, false, removed, server);

                if(removed) {
                    punishment.setRemovedBy(jsonDoc.get("removedBy").getAsString());
                    punishment.setRemovedAt(jsonDoc.get("removedAt").getAsLong());
                    punishment.setRemovedReason(jsonDoc.get("removedReason").getAsString());
                }

                this.punishments.add(punishment);
            }
        }

        this.loaded = true;

        Punishments.getInstance().getPunishDataManager().getDataMap().put(this.uuid, this);
    }

    public void saveAsync() {
        Tasks.runAsync(this::save);
    }

    public void save() {
        Document document = new Document();

        document.put("uuid", this.uuid.toString());
        document.put("name", this.name);

        document.put("ip", this.ip);

        JsonArray punished = new JsonArray();

        this.punished.forEach(punish -> {
            JsonObject jsonDoc = new JsonObject();

            jsonDoc.addProperty("name", punish.split("/")[0]);
            jsonDoc.addProperty("type", punish.split("/")[1].split("-")[0]);
            jsonDoc.addProperty("added", punish.split("-")[1]);

            punished.add(jsonDoc);
        });

        JsonArray alts = new JsonArray();

        this.alts.forEach(alt -> {
            JsonObject jsonDoc = new JsonObject();

            jsonDoc.addProperty("name", alt.split("/")[0]);
            jsonDoc.addProperty("type", alt.split("/")[1]);

            alts.add(jsonDoc);
        });

        JsonArray punishments = new JsonArray();

        this.punishments.forEach(punishment -> {
            JsonObject jsonDoc = new JsonObject();

            jsonDoc.addProperty("type", punishment.getType().name());
            jsonDoc.addProperty("addedBy", punishment.getSender());
            jsonDoc.addProperty("addedTo", punishment.getTarget());
            jsonDoc.addProperty("addedAt", punishment.getAdded());
            jsonDoc.addProperty("reason", punishment.getReason());
            jsonDoc.addProperty("duration", punishment.getDuration());
            jsonDoc.addProperty("removed", punishment.isRemoved());
            jsonDoc.addProperty("server", punishment.getServer());
            jsonDoc.addProperty("removedBy", punishment.getRemovedBy());
            jsonDoc.addProperty("removedAt", punishment.getRemovedAt());
            jsonDoc.addProperty("removedReason", punishment.getRemovedReason());

            punishments.add(jsonDoc);
        });

        document.put("punished", punished.toString());
        document.put("alts", alts.toString());
        document.put("punishments", punishments.toString());

        Punishments.getInstance().getDatabaseManager().getPunishments()
        .replaceOne(Filters.eq("uuid", this.uuid.toString()),
        document, new UpdateOptions().upsert(true));

        this.loaded = false;

        Punishments.getInstance().getPunishDataManager().getDataMap().remove(this.uuid);
    }
}
