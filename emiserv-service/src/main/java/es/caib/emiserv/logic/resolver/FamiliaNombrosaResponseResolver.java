/**
 * 
 */
package es.caib.emiserv.logic.resolver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import es.caib.emiserv.logic.helper.XmlHelper;

/**
 * Agregador per seleccionar la resposta pel cas del servei de famílies nombroses.
 * 
 * Les respostes possibles són les següents (Codi estat, Literal a retornar, Quan es retorna aquest missatge):
 * 
 * 	0 "Existe el titulo de familia numerosa"
 * 		Codi de resposta satisfactori: El títol de familia nombrosa existeix
 * 	1: "No hay registrada una persona titular o beneficiaria de familia numerosa con los datos de titular indicados", 
 * 		Amb les dades proveides en la consulta no es troba registrat cap titular o beneficiari de família nombrosa.
 * 	2: "No existe el titulo de familia numerosa indicado"
 * 		El titulo de família nombrosa indicat en la consulta no es troba registrat
 * 	3: "La persona no está asociada al título de familia numerosa indicado"
 * 		En el llibre de família nombrosa indicat no consta, ni com a titular ni com beneficiària, la persona les dades de la qual s'han indicat.
 * 	4: "Formato de titulo incorrecto"
 * 		El format del títol de família nombrosa no és correcte
 * 	5: "Con los datos facilitados existe más de una persona titular o beneficiario de un libro de familia numerosa
 * 		Amb les dades indicades existeix més d'una persona beneficiària o titular d'un llibre de família nombrosa. Aquest cas es podria produir quan la consulta es realitza per Nom, Cognom1 i Data de Naixement. La solució seria que indicar a més el numero de titulo.
 * 	6: "Título de familia numerosa caducado"
 * 		En el moment en què es consulta
 * 
 * La lògica per resoldre les respostes és la següent:
 * 
 * 1. Si es retorna una resposta amb codi 0 i la resta diferent. La resposta bona seria la de codi 0.
 * 2. Si es retorna més d'una resposta amb codi 0. La resposta bona seria la que té la "fecha de caducidad" més enfora.
 * 3. Si no es retorna cap resposta amb codi 0, es faria el següent (per ordre):
 * 	3.1 Si una resposta és 5, retornaria aquesta.
 * 	3.2 Si una resposta és 4, retornaria aquesta.
 * 	3.3 Si una resposta és 3, retornaria aquesta.
 * 	3.4 Si una resposta és 6, retornaria aquesta.
 * 	3.5 Si una resposta és 2, retornaria aquesta.
 * 	3.6 Si una resposta és 1, retornaria aquesta.
 * 4. En en el cas en què coincideixi més d'una resposta d'error igual, retornaria per mida de l'illa (Mallorca, Menorca, Eivissa).
 * 5. Si totes les respostes retornen un SOPAFault retornaria per mida de l'illa.
 * 
 */
@Component
public class FamiliaNombrosaResponseResolver implements ResponseResolver {

	private XmlHelper xmlHelper;

	public static final String XPATH_SOAPFAULT_FAULTCODE = "/soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode";
	public final static String XPATH_CODI_RESPOSTA  = "//datesp3:DatosEspecificos/datesp3:Retorno/datesp3:Estado/datesp3:CodigoEstado";
	public final static String XPATH_DATA_CADUCITAT = "//datesp3:DatosEspecificos/datesp3:Retorno/datesp3:TituloFamiliaNumerosaRetorno/datesp3:FechaCaducidad";

	/** Constructor per poder inserir l'xmlHelper */
	public FamiliaNombrosaResponseResolver() {
		this.xmlHelper = new XmlHelper();
	}
	public FamiliaNombrosaResponseResolver(XmlHelper xmlHelper) {
		this.xmlHelper = xmlHelper;
	}

	@Override
	public String resolve(
			List<String> clausOrdenades,
			Map<String, Document> responses) {
		String ret = null;
		if (responses != null && responses.size() > 0) {
				Document document;
				// 1 cerca la resposta 0 amb la data de caducitat més avançada
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date dataCaducitatMajor = null;
				Date dataCaducitat;
				int codi;
				String codi0 = null;
				String codi5 = null;
				String codi4 = null;
				String codi3 = null;
				String codi6 = null;
				String codi2 = null;
				String codi1 = null;
				String codiSOAPError = null;
				// Repassa totes les respostes i escull la que s'ha de retornar
				for (String entitatCodi : clausOrdenades != null? clausOrdenades : responses.keySet())
					try {
						if (responses.containsKey(entitatCodi)) {
							document = responses.get(entitatCodi);
							// Assegura que hi hagi resposta
							if (document != null ) {
								// Comprova si la resposta és un SOAPFault
								if (xmlHelper.getTextFromFirstNode(document, XPATH_SOAPFAULT_FAULTCODE, true) != null) {
									if (codiSOAPError == null)
										codiSOAPError = entitatCodi;							
								} else {
									codi = Integer.parseInt(xmlHelper.getTextFromFirstNode(document, XPATH_CODI_RESPOSTA));
									if (codi == 0) {
										dataCaducitat = sdf.parse(xmlHelper.getTextFromFirstNode(document, XPATH_DATA_CADUCITAT));
										if (dataCaducitatMajor == null || dataCaducitatMajor.before(dataCaducitat)) {
											codi0 = entitatCodi;
											dataCaducitatMajor = dataCaducitat;
										}
									} else if (codi == 5 && codi5 == null) {
										codi5 = entitatCodi;
									} else if (codi == 4 && codi4 == null) {
										codi4 = entitatCodi;
									} else if (codi == 3 && codi3 == null) {
										codi3 = entitatCodi;
									} else if (codi == 6 && codi6 == null) {
										codi6 = entitatCodi;
									} else if (codi == 2 && codi2 == null) {
										codi2 = entitatCodi;
									} else if (codi == 1 && codi1 == null) {
										codi1 = entitatCodi;
									}					
								}							}
						}
					} catch (Exception e) {
						System.err.println("Error llegint l'xml de resposta");
						e.printStackTrace(System.err);
					}				
				// Determina la resposta segons la prioritat dels codis
				if (codi0 != null)
					ret = codi0;
				else if (codi5 != null)
					ret = codi5;
				else if (codi4 != null)
					ret = codi4;
				else if (codi3 != null)
					ret = codi3;
				else if (codi6 != null)
					ret = codi6;
				else if (codi2 != null)
					ret = codi2;
				else if (codi1 != null)
					ret = codi1;
				else if (codiSOAPError != null)
					ret = codiSOAPError;
			}
		return ret;
	}

}
