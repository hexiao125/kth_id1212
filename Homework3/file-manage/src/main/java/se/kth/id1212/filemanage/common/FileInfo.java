package se.kth.id1212.filemanage.common;
import java.io.Serializable;

public class FileInfo implements Serializable {
    private final String name;
    private final String size;
    private final String owner;
    private boolean publicPrivacy = false;
    private boolean read = false;
    private boolean write = false;
    private boolean inform = false;   
    
    public FileInfo(String name, String size, String owner,
            String privacy, String read, String write, String inform) {
        this.name = name;
        this.size = size;
        this.owner = owner;
        
        if (privacy.equals("0")){
            this.publicPrivacy = false;
        } else {
            this.publicPrivacy = true;
        }
        if (read.equals("0")){
            this.read = false;
        } else {
            this.read = true;
        }
        if (write.equals("0")){
            this.write = false;
        } else {
            this.write = true;
        }
        if (inform.equals("0")){
            this.inform = false;
        } else {
            this.inform = true;
        }  
    }
    
    public String getName() {
        return this.name;
    }

    public String getSize() {
        return this.size;
    }

    public String getOwner() {
        return this.owner;
    }

    public boolean getPublicPrivacy() {
        return this.publicPrivacy;
    }

    public boolean getRead() {
        return this.read;
    }
    
    public boolean getWrite() {
        return this.write;
    }

    public boolean getInform() {
        return this.inform;
    }
            
}
