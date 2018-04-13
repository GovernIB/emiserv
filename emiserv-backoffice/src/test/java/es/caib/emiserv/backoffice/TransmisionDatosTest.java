/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.io.StringReader;
import java.util.Scanner;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import es.scsp.bean.common.DatosGenericos;

/**
 * Classe de prova per al processament dels DatosEspecificos de les
 * respostes dels backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TransmisionDatosTest {

	public static void main(String[] args) throws JAXBException {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(
				DatosEspecificosTest.class.getResourceAsStream("/backofficeResposta.xml")).
				useDelimiter("\\A");
		String xml = sc.next();
		sc.close();
		System.out.println(">>> " + getDatosGenericos(xml));
		System.out.println(">>> " + getDatosEspecificos(xml));
		DatosGenericos datosGenericos = JAXB.unmarshal(
				new StringReader(getDatosGenericos(xml)),
				DatosGenericos.class);
		System.out.println(">>> " + datosGenericos.getEmisor().getNombreEmisor());
	}

	public static String getDatosGenericos(String xml) {
		return getContingutEtiqueta(
				xml,
				"DatosGenericos",
				"datosGenericos");
	}
	public static String getDatosEspecificos(String xml) {
		return getContingutEtiqueta(
				xml,
				"DatosEspecificos",
				"datosEspecificos");
	}

	public static String getContingutEtiqueta(
			String xml,
			String... etiquetes) {
		int indexInici = -1;
		String etiquetaActual = null;
		for (String etiqueta: etiquetes) {
			etiquetaActual = etiqueta;
			indexInici = xml.indexOf(etiqueta);
			if (indexInici != -1) {
				break;
			}
		}
		if (indexInici != -1) {
			String etiquetaFi = etiquetaActual + ">";
			int indexFi = xml.indexOf(etiquetaFi, indexInici + etiquetaActual.length());
			int indexIniciTag = xml.substring(0, indexInici).lastIndexOf("<");
			return xml.substring(indexIniciTag, indexFi + etiquetaFi.length());
		} else {
			throw new RuntimeException("No s'ha trobat cap coincidència de les etiquetes a dins el missatge XML");
		}
	}

}
