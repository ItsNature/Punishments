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

    private int weight;
    private String ip;
    private List<String> punished;
    private List<String> alts;
    private Set<Punishment> punishments;

    private boolean loaded;

    public PunishData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.weight = 0;
        this.ip = "";
        this.punished = new ArrayList<>();
        this.alts = new ArrayList<>();
        this.punishments = new HashSet<>();

        this.loaded = false;
    }

    void load() {
        Document document = (Document) Punishments.getInstance().getMongo().getPunishments()
        .find(Filters.eq("uuid", this.uuid.toString())).first();

        if(document != null) {
            this.weight = document.getInteger("weight");
            this.ip = document.getString("ip");

            for(JsonElement doc : new JsonParser().parse(document.getString("punished")).getAsJsonArray()) {
                JsonObject json = doc.getAsJsonObject();

                this.punished.add(json.get("name").getAsString() + "/" + json.get("type").getAsString() + "-" + json.get("added").getAsLong());
            }

            for(JsonElement doc : new JsonParser().parse(document.getString("punishments")).getAsJsonArray()) {
                JsonObject json = doc.getAsJsonObject();

                PunishmentType type = PunishmentType.valueOf(json.get("type").getAsString());
                String addedBy = json.get("addedBy").getAsString();
                String addedTo = json.get("addedTo").getAsString();
                long addedAt = json.get("addedAt").getAsLong();
                String reason = json.get("reason").getAsString();
                long duration = json.get("duration").getAsLong();
                boolean removed = json.get("removed").getAsBoolean();
                String server = json.get("server").getAsString();

                Punishment punishment = new Punishment(type, addedBy, addedTo, addedAt, reason, duration, false, removed, server);

                if(removed) {
                    punishment.setRemovedBy(json.get("removedBy").getAsString());
                    punishment.setRemovedAt(json.get("removedAt").getAsLong());
                    punishment.setRemovedReason(json.get("removedReason").getAsString());
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

    void save() {
        Document document = new Document();

        document.put("uuid", this.uuid.toString());
        document.put("name", this.name);

        document.put("weight", this.weight);
        document.put("ip", this.ip);

        JsonArray punished = new JsonArray();

        this.punished.forEach(punish -> {
            JsonObject json = new JsonObject();

            json.addProperty("name", punish.split("/")[0]);
            json.addProperty("type", punish.split("/")[1].split("-")[0]);
            json.addProperty("added", punish.split("-")[1]);

            punished.add(json);
        });

        JsonArray alts = new JsonArray();

        this.alts.forEach(alt -> {
            JsonObject json = new JsonObject();

            json.addProperty("name", alt.split("/")[0]);
            json.addProperty("type", alt.split("/")[1]);

            alts.add(json);
        });

        JsonArray punishments = new JsonArray();

        this.punishments.forEach(punishment -> {
            JsonObject json = new JsonObject();

            json.addProperty("type", punishment.getType().name());
            json.addProperty("addedBy", punishment.getSender());
            json.addProperty("addedTo", punishment.getTarget());
            json.addProperty("addedAt", punishment.getAdded());
            json.addProperty("reason", punishment.getReason());
            json.addProperty("duration", punishment.getDuration());
            json.addProperty("removed", punishment.isRemoved());
            json.addProperty("server", punishment.getServer());
            json.addProperty("removedBy", punishment.getRemovedBy());
            json.addProperty("removedAt", punishment.getRemovedAt());
            json.addProperty("removedReason", punishment.getRemovedReason());

            punishments.add(json);
        });

        document.put("punished", punished.toString());
        document.put("alts", alts.toString());
        document.put("punishments", punishments.toString());

        Punishments.getInstance().getMongo().getPunishments()
        .replaceOne(Filters.eq("uuid", this.uuid.toString()),
        document, new UpdateOptions().upsert(true));

        this.loaded = false;

        Punishments.getInstance().getPunishDataManager().getDataMap().remove(this.uuid);
    }
}
