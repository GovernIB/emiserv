package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Resposta fake d'un servei enrutador: codi HTTP i missatge SOAP (resposta o SOAP Fault).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedireccioRespostaFakeDto implements Serializable {

	private int codiHttp;
	private String xml;

	private static final long serialVersionUID = 5381205537921064716L;
}
