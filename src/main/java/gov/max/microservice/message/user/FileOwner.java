package gov.max.microservice.message.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileOwner {

    private int id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String email;

    private String created_at;
    private int nextId; // to generate the random id
    private ArrayList<FileMetadata> fileMetadataList;

    public FileOwner(String name, String email) {
        this.id = nextId; // return generated id
        this.name = name;
        this.email = email;
        this.setCreated_at(); // set current date
        fileMetadataList = new ArrayList<>(); // create memory space for new
        // owner file data
    }

    public FileOwner() {
    } // Default constructor

    // Method returns Owner's FileMetadata by passing FileMetadata id as a parameter
    public FileMetadata getOwnerFileMetadata(String fileMetadataId) {
        FileMetadata fileMetadata = null;
        for (FileMetadata fm : this.fileMetadataList) {
            if (fm.getId().equals(fileMetadataId)) {
                fileMetadata = fm;
                break;
            }
        }
        return fileMetadata;
    }

    @JsonIgnore
    public ArrayList<String> getAllFileMetadataId() {
        FileMetadata fileMetadata = null;
        ArrayList<String> allFileMetadataId = new ArrayList<>();
        for (FileMetadata fm : this.fileMetadataList) {
            //System.out.println(p.getId());
            allFileMetadataId.add(fm.getId());
        }
        return allFileMetadataId;
    }

    public void deleteOwnerFileMetadata(String fileMetadataId) {
        // Return FileMetadata from moderator's FileMetadata list and then remove it.
        this.fileMetadataList.remove(getOwnerFileMetadata(fileMetadataId));

    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void putFileDataInList(FileMetadata p) {
        this.fileMetadataList.add(p);
    }

    public void setCreated_at() {
        LocalDateTime ldt = LocalDateTime.now();
        this.created_at = ldt.toString() + "Z";
    }

    @JsonIgnore
    public ArrayList<FileMetadata> getFileMetadataList() {
        return fileMetadataList;
    }

    public void setFileMetadataList(ArrayList<FileMetadata> fileMetadataList)
    {
        this.fileMetadataList = fileMetadataList;
    }


}
