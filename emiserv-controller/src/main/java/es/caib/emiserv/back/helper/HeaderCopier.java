package es.caib.emiserv.back.helper;

import org.apache.commons.httpclient.methods.PostMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

public interface HeaderCopier {
    void copiarCapsaleresHttp(HttpServletRequest request, PostMethod postMethod, String proxyUrl) throws MalformedURLException;
}
