/**
 * 
 */
package es.caib.emiserv.backoffice;

/**
 * Classe de prova per al processament dels DatosEspecificos de les
 * respostes dels backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatosEspecificosTest {

	private static final String DATOS_ESPECIFICOS = "<ns2:datosEspecificos xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Contenidor\"><Respuesta xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:Respuesta\"><ns2:DatosAlumno xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"><ns2:IdTitular xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"><ns2:TipoDocumentacion xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">NIF</ns2:TipoDocumentacion><ns2:Documentacion xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">12345678Z</ns2:Documentacion></ns2:IdTitular><ns2:Nombre xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">JOAN</ns2:Nombre><ns2:Apellido1 xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">BESTARD</ns2:Apellido1><ns2:Apellido2 xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">GONZA</ns2:Apellido2><ns2:FechaNacimiento xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">01/01/1970</ns2:FechaNacimiento></ns2:DatosAlumno><ns2:CodRespuesta xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">1</ns2:CodRespuesta><ns2:DescRespuesta xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">El titular de la consulta no està matriculat en un centre educatiu</ns2:DescRespuesta><ns2:FechaProceso xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">12/04/2016</ns2:FechaProceso></Respuesta></ns2:datosEspecificos>";

	public static void main(String[] args) {
		String datosEspecificosText = DATOS_ESPECIFICOS;
		String token1 = "DatosEspecificos";
		String token2 = "datosEspecificos";
		int indexDatespInici = datosEspecificosText.indexOf(token1);
		int indexDatespFi = 0;
		if (indexDatespInici != -1) {
			indexDatespFi = indexDatespInici + token1.length();
		} else {
			indexDatespInici = datosEspecificosText.indexOf(token2);
			if (indexDatespInici != -1) {
				indexDatespFi = indexDatespInici + token2.length();
			}
		}
		int indexFi = datosEspecificosText.indexOf(">");
		if (indexDatespInici != -1) {
			String datosEspecificosAmbNs1 = datosEspecificosText.substring(1, indexDatespInici) + token1;
			StringBuilder datosEspecificos = new StringBuilder();
			datosEspecificos.append("<");
			datosEspecificos.append(datosEspecificosAmbNs1);
			int indexXmlns = datosEspecificosText.indexOf("xmlns");
			int indexXmlnsDatesp = datosEspecificosText.indexOf("esquemas/datosespecificos");
			boolean conteXmlns = indexXmlns != -1 && indexXmlnsDatesp != -1 && indexXmlnsDatesp < indexFi;
			if (!conteXmlns) {
				String xmlns = " xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"";
				datosEspecificos.append(xmlns);
			}
			int indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs1);
			if (indexDatespBarraInici == -1) {
				String datosEspecificosAmbNs2 = datosEspecificosText.substring(1, indexDatespInici) + token2;
				indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs2);
			}
			datosEspecificos.append(datosEspecificosText.substring(indexDatespFi, indexDatespBarraInici + 1));
			datosEspecificos.append(datosEspecificosAmbNs1);
			datosEspecificos.append(">");
			System.out.println(">>> " + datosEspecificos.toString());
		}
	}

}
