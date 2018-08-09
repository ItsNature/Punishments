package gg.nature.punishments.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import gg.nature.punishments.Punishments;
import gg.nature.punishments.punish.Punishment;
import gg.nature.punishments.punish.PunishmentType;
import gg.nature.punishments.utils.Tasks;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class PunishData {

    private UUID uuid;
    private String name;

    private String ip;
    private List<String> punished;
    private Set<Punishment> punishments;

    private boolean loaded;

    public PunishData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.ip = "";
        this.punished = new ArrayList<>();
        this.punishments = new HashSet<>();

        this.loaded = false;
    }

    public List<String> getAllPunished() {
        return this.punished.stream().filter(punish -> !punish.equals("")).collect(Collectors.toList());
    }

    public void loadAsync() {
        Tasks.runAsync(this::load);
    }

    public void load() {
        Document document = (Document) Punishments.getInstance().getDatabaseManager().getPunishments()
        .find(Filters.eq("uuid", this.uuid.toString())).first();

        if(document == null) {
            this.save(false);
            this.loadAsync();
            return;
        }

        this.ip = document.getString("ip");

        this.punished.addAll(Arrays.asList(document.get("punished").toString()
        .replace("[", "").replace("]", "").replace(" ", "").split(",")));

        for(JsonElement doc : new JsonParser().parse(document.getString("punishments")).getAsJsonArray()) {
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

        this.loaded = true;

        Punishments.getInstance().getPunishDataManager().getDataMap().put(this.uuid, this);
    }

    public void saveAsync(boolean remove) {
        Tasks.runAsync(() -> this.save(remove));
    }

    public void save(boolean remove) {
        Document document = new Document();

        document.put("uuid", this.uuid.toString());
        document.put("name", this.name);

        document.put("ip", this.ip);

        document.put("punished", this.punished.toString());

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

        document.put("punishments", punishments.toString());

        Punishments.getInstance().getDatabaseManager().getPunishments()
        .replaceOne(Filters.eq("uuid", this.uuid.toString()),
        document, new UpdateOptions().upsert(true));

        this.loaded = false;

        if(remove) Punishments.getInstance().getPunishDataManager().getDataMap().remove(this.uuid);
    }
}
