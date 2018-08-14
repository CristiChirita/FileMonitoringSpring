package com.nttdata.filemonitor.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A Folder.
 */
@Document(collection = "folder")
public class Folder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("hash")
    private String hash;

    @Field("name")
    private String name;

    @Field("files")
    private List<Files> files;

    public List<Files> getFiles() {
        return files;
    }

    public Folder setFiles(List<Files> files) {
        this.files = files;
        return this;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public Folder hash(String hash) {
        this.hash = hash;
        return this;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public Folder name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Folder folder = (Folder) o;
        if (folder.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), folder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Folder{" +
            "id=" + getId() +
            ", hash='" + getHash() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
