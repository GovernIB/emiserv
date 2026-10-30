package es.caib.emiserv.back.helper;

import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("mock")
public class HeaderCopierMock implements HeaderCopier {

    private static final Set<String> HOP_BY_HOP = new HashSet<>(Arrays.asList(
            "connection","keep-alive","proxy-authenticate","proxy-authorization",
            "te","trailer","transfer-encoding","upgrade","proxy-connection"
    ));

    @Override
    public void copiarCapsaleresHttp(HttpServletRequest request, PostMethod postMethod, String proxyUrl)
            throws MalformedURLException {

        Set<String> connectionTokens = new HashSet<>();
        String connectionHeader = request.getHeader("Connection");
        if (connectionHeader != null) {
            for (String token : connectionHeader.split(",")) {
                if (!token.trim().isEmpty()) connectionTokens.add(token.trim().toLowerCase());
            }
        }

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String lower = header.toLowerCase();

            if (HOP_BY_HOP.contains(lower) || connectionTokens.contains(lower)) continue;
            if ("content-length".equals(lower) || "expect".equals(lower)) continue;

            String value;
            if ("host".equals(lower)) {
                URL url = new URL(proxyUrl);
                int port = url.getPort();
                value = url.getHost() + ((port != -1) ? ":" + port : "");
            } else {
                value = request.getHeader(header);
            }

            if (value != null) postMethod.setRequestHeader(header, value);
        }
    }
}