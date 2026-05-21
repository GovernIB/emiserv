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
        // Monitor en memòria per a estadístiques d'integració (keyed per "solicitantId|serveiCodi" o "serveiCodi")
        MonitorComponentsMemoria monitorIntegracio = new MonitorComponentsMemoria(20);
        integracioComponentHelper = new SalutComponentsHelper(monitorIntegracio, (codi) -> false, Collections.emptyList());
    }

    public static void addSubsistemaExit(SubsistemesEnum subsistema, long duracioMs) {
        subsistemaComponentHelper.registraExit(subsistema.name(), duracioMs);
    }

    public static void addSubsistemaExit(SubsistemesEnum subsistema, String serveiCodi, long duracioMs) {
        subsistemaComponentHelper.registraExit(subsistema.name(), duracioMs);
    }

    public static void addSubsistemaError(SubsistemesEnum subsistema) {
        subsistemaComponentHelper.registraError(subsistema.name());
    }

    public static void addSubsistemaError(SubsistemesEnum subsistema, String serveiCodi) {
        subsistemaComponentHelper.registraError(subsistema.name());
    }

    public static List<SubsistemaSalut> getSubsistemesSalut() {
        return subsistemaComponentHelper.obtenInforme().toSubsistemesSalut();
    }

    public static void addIntegracioExit(String integracioCodi, long duracioMs) {
        if (teText(integracioCodi)) {
            integracioComponentHelper.registraExit(integracioCodi, duracioMs);
        }
    }

    public static void addIntegracioExit(String solicitantId, String serveiCodi, long duracioMs) {
        String integracioCodi = getIntegracioCodi(solicitantId, serveiCodi);
        if (integracioCodi != null) {
            integracioComponentHelper.registraExit(integracioCodi, duracioMs);
        }
    }

    public static void addIntegracioError(String solicitantId, String serveiCodi) {
        String integracioCodi = getIntegracioCodi(solicitantId, serveiCodi);
        if (integracioCodi != null) {
            integracioComponentHelper.registraError(integracioCodi);
        }
    }

    public static List<SubsistemaSalut> getIntegracioStats() {
        return integracioComponentHelper.obtenInforme().toSubsistemesSalut();
    }

    private static String getIntegracioCodi(String solicitantId, String serveiCodi) {
        if (!teText(serveiCodi)) {
            return null;
        }
        if (teText(solicitantId)) {
            return solicitantId + "|" + serveiCodi;
        }
        return serveiCodi;
    }

    private static boolean teText(String text) {
        return text != null && !text.trim().isEmpty();
    }

}
