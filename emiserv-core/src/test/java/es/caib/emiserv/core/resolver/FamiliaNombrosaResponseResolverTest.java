package es.caib.emiserv.core.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.caib.emiserv.core.helper.XmlHelper;

/** Classe de proves del resolutor de respostes del servei de famílies nombroses.  Realitza 
 * un joc de proves unitàries.
 */
public class FamiliaNombrosaResponseResolverTest {
	
	private XmlHelper xmlHelper;
	private String familiaNombrosaSampleResponseXml = null;
	private String soapFaultSampleResponseXml = null;
	private List<String> clausOrdenades;
	
	/** Constructor per inicialitzar el test. */
	public FamiliaNombrosaResponseResolverTest() {
		this.xmlHelper = new XmlHelper();
		try {
			this.familiaNombrosaSampleResponseXml = 
					IOUtils.toString(new ClassPathResource("/es/caib/emiserv/core/resolver/FamiliaNombrosaSampleResponse.xml").getInputStream());
			this.soapFaultSampleResponseXml = 
					IOUtils.toString(new ClassPathResource("/es/caib/emiserv/core/resolver/SOAPFaultSampleResponse.xml").getInputStream());
			this.clausOrdenades = Arrays.asList("MALLORCA", "MENORCA", "EIVISSA", "FORMENTERA");
		} catch (Exception e) {
			System.err.println("No s'ha pogut carregar el fitxer de resposta d'expemple pel FamiliaNombrosaResponseResolverTest");
			e.printStackTrace();
		}
	}
	
	/** Comrova que es retorna null si no hi ha cap resposta o es passa null. */
	@Test
	public void respostaNullSenseCapResposta() {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver();
		assertNull(resolver.resolve(this.clausOrdenades, null));
		assertNull(resolver.resolve(this.clausOrdenades, new HashMap<String, Document>()));		
	}
	
	/** Comprova per totes les respostes amb codi 0 que retorna la resposta amb la data de caducitat
	 * més allunyada.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta0PerDataCaducitat() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("MALLORCA", 	this.getDocument("0", "01/01/2017"));
		respostes.put("MENORCA",  	this.getDocument("0", "01/01/2018"));
		respostes.put("EIVISSA",  	this.getDocument("0", "01/01/2019"));
		respostes.put("FORMENTERA", this.getDocument("0", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("0", "01/01/2021"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA", 	this.getDocument("0", "01/01/2022"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA", 	this.getDocument("0", "01/01/2023"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
	}
	
	/** Comprova que si totes les respostes són 1 llavors retorna el codi de la illa major.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta5PerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getDocument("5", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("5", "01/01/2019"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("5", "01/01/2018"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("5", "01/01/2017"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}

	/** Comprova que si totes les respostes són 1 llavors retorna el codi de la illa major.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta4PerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getDocument("4", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("4", "01/01/2019"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("4", "01/01/2018"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("4", "01/01/2017"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}

	/** Comprova que si totes les respostes són 1 llavors retorna el codi de la illa major.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta3PerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getDocument("3", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("3", "01/01/2019"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("3", "01/01/2018"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("3", "01/01/2017"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}

	/** Comprova que si totes les respostes són 1 llavors retorna el codi de la illa major.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta6PerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getDocument("6", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("6", "01/01/2019"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("6", "01/01/2018"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("6", "01/01/2017"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}

	/** Comprova que si totes les respostes són 1 llavors retorna el codi de la illa major.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta2PerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getDocument("2", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("2", "01/01/2019"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("2", "01/01/2018"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("2", "01/01/2017"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}

	/** Comprova que si totes les respostes són 1 llavors retorna el codi de la illa major.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void resposta1PerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getDocument("1", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("1", "01/01/2019"));
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("1", "01/01/2018"));
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("1", "01/01/2017"));
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}
	
	/** Comprova que es van acomplint les prioritats entre missatges i el tamany de les illes.
	 * La prioritat és 0, 5, 4, 3, 6, 2, i 1
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void respostaAmbCodisCombinats() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("MENORCA", 	this.getDocument("1", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("1", "01/01/2020"));
		assertEquals("MALLORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		
		respostes.put("MENORCA",  	this.getDocument("2", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("2", "01/01/2020"));
		assertEquals("MALLORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		
		respostes.put("MENORCA",  	this.getDocument("6", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("6", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA",  	this.getDocument("6", "01/01/2020"));
		assertEquals("MALLORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		
		respostes.put("FORMENTERA", this.getDocument("3", "01/01/2020"));
		assertEquals("FORMENTERA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getDocument("3", "01/01/2020"));
		assertEquals("EIVISSA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("3", "01/01/2020"));
		assertEquals("MALLORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		
		respostes.put("MENORCA",  	this.getDocument("4", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getDocument("4", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA",  	this.getDocument("4", "01/01/2020"));
		assertEquals("MALLORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		
		respostes.put("FORMENTERA", this.getDocument("0", "01/01/2020"));
		assertEquals("FORMENTERA", 	resolver.resolve(this.clausOrdenades, respostes));
	}	
	
	/** Si es passa un document que no conté ni codi ni data de caducitat s'espera que
	 * tracti correctament la resta.
	 */
	@Test
	public void respostesCorrectes3DocumetIncorrecte1() throws Exception{
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);

	    Document emptyDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", 	this.getDocument("1", "01/01/2020"));
		assertEquals("FORMENTERA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA", 	emptyDocument);
		assertEquals("FORMENTERA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA", 	this.getDocument("1", "01/01/2020"));
		assertEquals("MENORCA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getDocument("1", "01/01/2020"));
		assertEquals("MALLORCA", 	resolver.resolve(this.clausOrdenades, respostes));
	}
	
	/** Si es passa un document null s'espera que no es tracti
	 */
	@Test
	public void respostesCorrectes1RespostaNull1() throws Exception{
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);

		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", 	this.getDocument("1", "01/01/2020"));
		assertEquals("FORMENTERA", 	resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	null);
		assertEquals("FORMENTERA", 	resolver.resolve(this.clausOrdenades, respostes));
		
	}

	/** Comprova que si només hi ha respostes amb error restorna la resposta error de la illa major.
	 */
	@Test
	public void documentsIncorrectesRespostaError() throws Exception{
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);

	    Document emptyDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA",	emptyDocument);
		assertNull(resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA", emptyDocument);
		assertNull(resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA", emptyDocument);
		assertNull(resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	emptyDocument);
		assertNull(resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("FORMENTERA", this.getDocument("1", "01/01/2020"));
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
	}

	/** Comprova quan totes les respostes són SOAPFault que retorni la resposta de l'illa més grossa. */
	@Test
	public void respostaSoapFaultPerTamanyIlla() throws Exception {
		ResponseResolver resolver = new FamiliaNombrosaResponseResolver(xmlHelper);
		Map<String, Document> respostes = new HashMap<String, Document>();
		respostes.put("FORMENTERA", this.getSOAPFaultDocument());
		assertEquals("FORMENTERA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("EIVISSA",  	this.getSOAPFaultDocument());
		assertEquals("EIVISSA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MENORCA",  	this.getSOAPFaultDocument());
		assertEquals("MENORCA", resolver.resolve(this.clausOrdenades, respostes));
		respostes.put("MALLORCA", 	this.getSOAPFaultDocument());
		assertEquals("MALLORCA", resolver.resolve(this.clausOrdenades, respostes));
	}
	
	/** Mètode privat per construir un document i retornar el seu primer element.
	 * 
	 * @param codiResposta
	 * @param dataCaducitat
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private Document getDocument(
			String codiResposta, 
			String dataCaducitat) throws Exception {
		String xml = this.familiaNombrosaSampleResponseXml;
		Document document = xmlHelper.bytesToDocument(xml.getBytes());	
		xmlHelper.setTextToFirstNode(document, FamiliaNombrosaResponseResolver.XPATH_CODI_RESPOSTA, codiResposta);
		xmlHelper.setTextToFirstNode(document, FamiliaNombrosaResponseResolver.XPATH_DATA_CADUCITAT, dataCaducitat);
		return document;
	}
	
	private Document getSOAPFaultDocument() throws Exception{
		String xml = this.soapFaultSampleResponseXml;
		Document document = xmlHelper.bytesToDocument(xml.getBytes());	
		return document;
	}
}
