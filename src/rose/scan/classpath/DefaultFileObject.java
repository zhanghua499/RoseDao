package rose.scan.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.util.ResourceUtils;

public class DefaultFileObject implements FileObject{
    private final URL url;

    private final String urlString;

    private final File file;

    private final FileSystemManager fs;

    DefaultFileObject(FileSystemManager fs, URL url) throws FileNotFoundException,
            MalformedURLException {
        this.fs = fs;
        File file = ResourceUtils.getFile(url);
        String urlString = url.toString();
        this.url = url;
        this.file = file;
        this.urlString = urlString;
    }

    
    public FileObject getChild(final String child) throws IOException {
        return fs.resolveFile(urlString + child);
    }

    
    public FileObject[] getChildren() throws MalformedURLException, IOException {
        File[] files = file.listFiles();
        FileObject[] children = new FileObject[files.length];
        for (int i = 0; i < children.length; i++) {
            if (files[i].isDirectory()) {
                children[i] = fs.resolveFile(urlString + files[i].getName() + "/");
            } else {
                children[i] = fs.resolveFile(urlString + files[i].getName());
            }
        }
        return children;
    }
  
    public FileObject getParent() throws MalformedURLException, IOException {
        File parent = file.getParentFile();
        if (parent == null) {
            return null;
        }
        return fs.resolveFile(parent.toURI().toURL());
    }

    
    public FileType getType() {
        if (file.isFile()) {
            return FileType.FILE;
        } else if (file.isDirectory()) {
            return FileType.FOLDER;
        }
        return FileType.UNKNOWN;
    }

    
    public URL getURL() throws MalformedURLException {
        return url;
    }

    
    public boolean exists() throws IOException {
        return file.exists();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultFileObject)) {
            return false;
        }
        DefaultFileObject t = (DefaultFileObject) obj;
        return this.file.equals(t.file);
    }
    
    public FileInputStream getContent() throws IOException {
        if (!file.canRead()) {
            throw new IOException("can not read");
        }
        
        return new FileInputStream(file);
    }
    
    public String getRelativeName(String subPath) throws IOException{
    	String basePath = this.getURL().getPath();  	
        if (!subPath.startsWith(basePath)) {
            throw new IllegalArgumentException("basePath='" + basePath + "'; subPath='" + subPath
                    + "'");
        }
        return subPath.substring(basePath.length());
    }

    public int hashCode() {
        return file.hashCode() * 13;
    }

    
    public String toString() {
        return urlString;
    }


	public String getName() throws IOException {	
		return file.getName();
	}

}
