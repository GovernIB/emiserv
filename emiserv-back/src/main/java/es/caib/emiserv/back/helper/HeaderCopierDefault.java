package es.caib.emiserv.back.helper;

import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

@Component
@Profile("!mock")
public class HeaderCopierDefault implements HeaderCopier {

    @Override
    public void copiarCapsaleresHttp(HttpServletRequest request, PostMethod postMethod, String proxyUrl)
            throws MalformedURLException {

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String value;
            if (header.equalsIgnoreCase("host")) {
                URL url = new URL(proxyUrl);
                int port = url.getPort();
                value = url.getHost() + ((port != -1) ? ":" + port : "");
            } else {
                value = request.getHeader(header);
            }
            postMethod.setRequestHeader(header, value);
        }
    }
}