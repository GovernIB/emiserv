package es.caib.emiserv.logic.intf.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum SubsistemesEnum {
    BCK_SYN("Backoffice - Síncrona", true),
    BCK_AS("Backoffice - Asíncrona", true),
    BCK_SR("Backoffice - Sol.licitud resposta", true),
    ENR_S("Enrutador simple", true),
    ENR_M("Enrutador múltiple", true);

    private final String nom;
    private final boolean sistemaCritic;

    SubsistemesEnum(String nom, boolean sistemaCritic) {
        this.nom = nom;
        this.sistemaCritic = sistemaCritic;
    }

    public static SubsistemesEnum valueOfCodi(String codi) {
        for (SubsistemesEnum subsistema : SubsistemesEnum.values()) {
            if (subsistema.name().equals(codi)) {
                return subsistema;
            }
        }
        return null;
    }

    public static final Set<String> CODIS =
            Arrays.stream(values())
                    .map(SubsistemesEnum::name)
                    .collect(Collectors.toSet());

    public static boolean containsCodi(String codi) {
        return CODIS.contains(codi);
    }
}