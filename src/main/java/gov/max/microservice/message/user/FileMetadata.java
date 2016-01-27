package gov.max.microservice.message.user;

import gov.max.microservice.message.api.DisplayResult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class FileMetadata {

    @JsonView(DisplayResult.withoutResults.class)
    private String id;

    @NotNull
    @NotEmpty
    @JsonView(DisplayResult.withoutResults.class)
    private String name;

    @NotNull
    @NotEmpty
    @JsonView(DisplayResult.withoutResults.class)
    private String started_at;

    @NotNull
    @NotEmpty
    @JsonView(DisplayResult.withoutResults.class)
    private String expired_at;

    private int flag;

    public FileMetadata(String name, String started_at, String expired_at) {
        this.setId("543212"); // This is default, will override in controller
        this.name = name;
        this.started_at = started_at;
        this.expired_at = expired_at;
    }

    public FileMetadata() {
    }

    @JsonIgnore
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

}
