package rose.scan.classpath;

public class FileType {

    public static final FileType FOLDER = new FileType(true, false);

    public static final FileType FILE = new FileType(false, true);

    public static final FileType UNKNOWN = new FileType(false, false);

    private boolean hasChildren = false;

    private boolean hasContent = false;

    private FileType(boolean hasChildren, boolean hasContent) {
        this.hasChildren = hasChildren;
        this.hasContent = hasContent;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public boolean hasContent() {
        return hasContent;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileType)) {
            return false;
        }
        FileType t = (FileType) obj;
        return hasChildren == t.hasChildren && hasContent == t.hasContent;
    }

    @Override
    public String toString() {
        return hasChildren ? "FOLDER" : hasContent ? "FILE" : "UNKNOWN";
    }

}
