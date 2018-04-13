/**
 * 
 */
package es.caib.emiserv.war.helper;

import org.springframework.stereotype.Component;

/**
 * Helper per a obtenir recursos dels webjars.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class WebjarsHelper {

	public static final String VFS_PREFIX = "vfszip:";

	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<Resource> createWebjarResponse(
			HttpServletRequest request,
			String webjar) {
		String mvcPath = (String)request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String fullPath = "META-INF/resources" + mvcPath;
		try {
			Resource resource = new ClassPathResource(fullPath);
			String resourceUrl = resource.getURL().toString();
			if (resourceUrl.startsWith(VFS_PREFIX)) {
				FileObject file = vfsResourceFile(resourceUrl);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setContentLength(file.getContent().getSize());
				return new ResponseEntity(
						new InputStreamResource(
								file.getContent().getInputStream()),
						httpHeaders,
						HttpStatus.OK);
			} else {
				return new ResponseEntity(
						resource,
						HttpStatus.OK);
			}
		} catch (IOException ex) {
			logger.error("No s'ha pogut obtenir el webjar " + fullPath, ex);
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
	}

	@PreDestroy
    public void destroy() {
        try {
        	logger.info("Tancant el FileSystemManager");
        	DefaultFileSystemManager fsManager = (DefaultFileSystemManager)VFS.getManager();
        	fsManager.close();
        } catch (FileSystemException ex) {
        	logger.error("No s'ha pogut tancar el FileSystemManager", ex);
        }
    }



	private FileObject vfsResourceFile(String url) throws FileSystemException {
		int numjars = 0;
		numjars += countOccurrences(url, ".ear/");
		numjars += countOccurrences(url, ".war/");
		numjars += countOccurrences(url, ".jar/");
		StringBuilder processedUrl = new StringBuilder();
		for (int i = 0; i < numjars; i++) {
			processedUrl.append("jar:");
		}
		processedUrl.append(
				url.substring(VFS_PREFIX.length()).
				replace(".ear/", ".ear!/").
				replace(".war/", ".war!/").
				replace(".jar/", ".jar!/"));
		DefaultFileSystemManager fsManager = (DefaultFileSystemManager)VFS.getManager();
		return fsManager.resolveFile(processedUrl.toString());
	}



	private static int countOccurrences(String str, String substr) {
		int lastIndex = 0;
		int count = 0;
		while (lastIndex != -1) {
		    lastIndex = str.indexOf(substr,lastIndex);
		    if (lastIndex != -1) {
		        count ++;
		        lastIndex += substr.length();
		    }
		}
		return count;
	}

	private static final Logger logger = LoggerFactory.getLogger(WebjarsHelper.class);*/

}
