package es.scsp.backoffice;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import org.slf4j.Logger;

import java.util.Arrays;

/**
 * Adapta les classes de backoffice compilades contra les llibreries SCSP 4 a la interfície
 * es.scsp.common.backoffice.BackOffice de les llibreries SCSP 5.
 *
 * La interfície té el mateix nom i els mateixos mètodes a les dues versions, però els tipus
 * dels paràmetres i dels valors de retorn són diferents (es.scsp.bean.common.Peticion a SCSP 4,
 * es.scsp.bean.common.peticion.Peticion a SCSP 5). Una classe de backoffice antiga es carrega
 * sense errors, però l'emisor SCSP 5 obtendria un AbstractMethodError en cridar-la.
 *
 * Per a cada mètode amb la signatura SCSP 4 s'afegeix un mètode pont amb la signatura SCSP 5
 * que converteix els beans amb {@link LegacyBackofficeConverter} i crida el mètode original.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LegacyBackofficeBridge {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(LegacyBackofficeBridge.class);

    private static final String BACKOFFICE_INTERFACE = "es.scsp.common.backoffice.BackOffice";
    private static final String CONVERTER = LegacyBackofficeConverter.class.getName();

    private static final BridgeMethod[] BRIDGE_METHODS = {
            new BridgeMethod(
                    "NotificarSincrono",
                    "(Les/scsp/bean/common/Peticion;)Les/scsp/bean/common/Respuesta;",
                    "(Les/scsp/bean/common/peticion/Peticion;)Les/scsp/bean/common/respuesta/Respuesta;",
                    "es.scsp.bean.common.respuesta.Respuesta",
                    "es.scsp.bean.common.peticion.Peticion"),
            new BridgeMethod(
                    "NotificarAsincrono",
                    "(Les/scsp/bean/common/Peticion;)Les/scsp/bean/common/ConfirmacionPeticion;",
                    "(Les/scsp/bean/common/peticion/Peticion;)Les/scsp/bean/common/confirmacion/ConfirmacionPeticion;",
                    "es.scsp.bean.common.confirmacion.ConfirmacionPeticion",
                    "es.scsp.bean.common.peticion.Peticion"),
            new BridgeMethod(
                    "SolicitudRespuesta",
                    "(Les/scsp/bean/common/SolicitudRespuesta;)Les/scsp/bean/common/Respuesta;",
                    "(Les/scsp/bean/common/solicitud/SolicitudRespuesta;)Les/scsp/bean/common/respuesta/Respuesta;",
                    "es.scsp.bean.common.respuesta.Respuesta",
                    "es.scsp.bean.common.solicitud.SolicitudRespuesta")
    };

    /**
     * Si la classe és un backoffice compilat contra les llibreries SCSP 4 li afegeix els
     * mètodes pont necessaris per a implementar la interfície BackOffice de SCSP 5.
     *
     * @return true si s'ha adaptat la classe.
     */
    public static boolean adapt(CtClass ctClass) throws CannotCompileException {
        if (ctClass.isInterface() || Modifier.isAbstract(ctClass.getModifiers())) {
            return false;
        }
        if (!Arrays.asList(ctClass.getClassFile().getInterfaces()).contains(BACKOFFICE_INTERFACE)) {
            return false;
        }
        boolean adapted = false;
        for (BridgeMethod bridgeMethod: BRIDGE_METHODS) {
            if (hasDeclaredMethod(ctClass, bridgeMethod.name, bridgeMethod.legacyDescriptor) &&
                    !hasDeclaredMethod(ctClass, bridgeMethod.name, bridgeMethod.descriptor)) {
                ctClass.addMethod(CtNewMethod.make(bridgeMethod.getSource(), ctClass));
                adapted = true;
            }
        }
        if (adapted) {
            log.warn("BKO - La classe de backoffice {} està compilada contra les llibreries SCSP 4. " +
                    "S'hi han afegit els mètodes de compatibilitat amb SCSP 5. Es recomana recompilar-la " +
                    "contra les llibreries SCSP 5.", ctClass.getName());
        }
        return adapted;
    }

    private static boolean hasDeclaredMethod(CtClass ctClass, String name, String descriptor) {
        for (CtMethod method: ctClass.getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getSignature().equals(descriptor)) {
                return true;
            }
        }
        return false;
    }

    private static class BridgeMethod {
        private final String name;
        private final String legacyDescriptor;
        private final String descriptor;
        private final String returnType;
        private final String parameterType;

        BridgeMethod(String name, String legacyDescriptor, String descriptor, String returnType, String parameterType) {
            this.name = name;
            this.legacyDescriptor = legacyDescriptor;
            this.descriptor = descriptor;
            this.returnType = returnType;
            this.parameterType = parameterType;
        }

        String getSource() {
            // El compilador de Javassist tria el mètode original pel tipus del paràmetre (bean SCSP 4)
            return "public " + returnType + " " + name + "(" + parameterType + " param) " +
                    "throws es.scsp.common.exceptions.ScspException {" +
                    " return " + CONVERTER + ".fromLegacy(this." + name + "(" + CONVERTER + ".toLegacy(param)));" +
                    " }";
        }
    }

}
