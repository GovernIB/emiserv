package es.caib.emiserv.logic.helper;

import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.salut.helper.SalutComponentsHelper;
import es.caib.comanda.ms.salut.helper.components.MonitorComponentsMemoria;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class SalutHelper {

    private static SalutComponentsHelper subsistemaComponentHelper;
    static {
        // Monitor en memòria (fallback de 20 peticions per component)
        MonitorComponentsMemoria monitor = new MonitorComponentsMemoria(20);
        Function<String, Boolean> esCritic = SubsistemesEnum::containsCodi;
        subsistemaComponentHelper = new SalutComponentsHelper(monitor, esCritic, SubsistemesEnum.CODIS);
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

}
