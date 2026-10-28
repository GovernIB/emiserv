package es.caib.emiserv.logic.intf.base.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Arxiu per descarregar.
 * 
 * @author Limit Tecnologies
 */
@Getter @Setter
@AllArgsConstructor
public class DownloadableFile {

	private String name;
	private String contentType;
	private byte[] content;

	public Long getContentLength() {
		if (content != null) {
			return Long.valueOf(content.length);
		} else {
			return null;
		}
	}

}
