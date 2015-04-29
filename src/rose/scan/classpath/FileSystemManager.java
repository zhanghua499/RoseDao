package rose.scan.classpath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ResourceUtils;

public class FileSystemManager {

    protected Log logger = LogFactory.getLog(FileSystemManager.class);

    @SuppressWarnings("unchecked")
    private Map<String, FileObject> cached = new LRUMap(10000);

    public FileObject resolveFile(String urlString) throws IOException {

        FileObject object = cached.get(urlString);
        if (object == null && !urlString.endsWith("/")) {
            object = cached.get(urlString + "/");
        }
        if (object != null) {
            return object;
        }
        return resolveFile(new URL(urlString));
    }

    public synchronized FileObject resolveFile(URL url) throws IOException {
        try {
            String urlString = url.toString();
            FileObject object = cached.get(urlString);

            if (object != null) {
                return object;
            }
            if (ResourceUtils.isJarURL(url)) {
            	
            } else {
                File file = ResourceUtils.getFile(url);
                if (file.isDirectory()) {
                    if (!urlString.endsWith("/")) {
                        urlString = urlString + "/";
                        url = new URL(urlString);
                    }
                } else if (file.isFile()) {
                    if (urlString.endsWith("/")) {
                        urlString = StringUtils.removeEnd(urlString, "/");
                        url = new URL(urlString);
                    }
                }
                object = new DefaultFileObject(this, url);
            }
            if (object.exists()) {
                cached.put(urlString, object);
            }
            return object;
        } catch (IOException e) {
            throw e;
        }
    }

    public synchronized void clearCache() {
        cached.clear();
    }

}
