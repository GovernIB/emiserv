package es.caib.emiserv.logic.helper;

import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.salut.helper.SalutComponentsHelper;
import es.caib.comanda.ms.salut.helper.components.MonitorComponentsMemoria;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Component
public class SalutHelper {

    private static SalutComponentsHelper subsistemaComponentHelper;
    private static SalutComponentsHelper integracioComponentHelper;
    static {
        // Monitor en memòria (fallback de 20 peticions per component)
        MonitorComponentsMemoria monitor = new MonitorComponentsMemoria(20);
        Function<String, Boolean> esCritic = SubsistemesEnum::containsCodi;
        subsistemaComponentHelper = new SalutComponentsHelper(monitor, esCritic, SubsistemesEnum.CODIS);
        // Monitor en memòria per a estadístiques d'integració (keyed per "solicitantId|serveiCodi")
        MonitorComponentsMemoria monitorIntegracio = new MonitorComponentsMemoria(20);
        integracioComponentHelper = new SalutComponentsHelper(monitorIntegracio, (codi) -> false, Collections.emptyList());
    }

    public static void addSubsistemaExit(SubsistemesEnum subsistema, long duracioMs) {
		subsistemaComponentHelper.registraExit(subsistema.name(), duracioMs);
	}
    public static void addSubsistemaExit(SubsistemesEnum subsistema, String serveiCodi, long duracioMs) {
        subsistemaComponentHelper.registraExit(subsistema.name(), duracioMs);
        subsistemaComponentHelper.registraExit(serveiCodi, duracioMs);
    }

	public static void addSubsistemaError(SubsistemesEnum subsistema) {
		subsistemaComponentHelper.registraError(subsistema.name());
	}
    public static void addSubsistemaError(SubsistemesEnum subsistema, String serveiCodi) {
        subsistemaComponentHelper.registraError(subsistema.name());
        subsistemaComponentHelper.registraError(serveiCodi);
    }

    public static List<SubsistemaSalut> getSubsistemesSalut() {
        return subsistemaComponentHelper.obtenInforme().toSubsistemesSalut();
    }

    public static void addIntegracioExit(String integracioCodi, long duracioMs) {
        if (integracioCodi != null) {
            integracioComponentHelper.registraExit(integracioCodi, duracioMs);
        }
    }

    public static void addIntegracioExit(String solicitantId, String serveiCodi, long duracioMs) {
        if (solicitantId != null && serveiCodi != null) {
            integracioComponentHelper.registraExit(solicitantId + "|" + serveiCodi, duracioMs);
        }
    }

    public static void addIntegracioError(String solicitantId, String serveiCodi) {
        if (solicitantId != null && serveiCodi != null) {
            integracioComponentHelper.registraError(solicitantId + "|" + serveiCodi);
        }
    }

    public static List<SubsistemaSalut> getIntegracioStats() {
        return integracioComponentHelper.obtenInforme().toSubsistemesSalut();
    }

}
