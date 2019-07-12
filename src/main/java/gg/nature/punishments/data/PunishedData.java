package gg.nature.punishments.data;

import gg.nature.punishments.punish.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PunishedData {

    private String name;
    private PunishmentType type;
    private long added;
}
