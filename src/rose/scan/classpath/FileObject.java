package rose.scan.classpath;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public interface FileObject {

    boolean exists() throws IOException;

    FileObject[] getChildren() throws IOException;

    FileType getType() throws IOException;

    URL getURL() throws IOException;

    FileObject getParent() throws IOException;

    FileObject getChild(String name) throws IOException;

    FileInputStream getContent() throws IOException;
    
    String getRelativeName(String subFilePath) throws IOException;
    
    String getName() throws IOException;
    
    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

}
