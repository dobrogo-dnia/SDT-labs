package model;

import jakarta.persistence.*;

@Entity
@Table(name="files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String location;

    @ManyToOne
    @JoinColumn(nullable = false, name="ownerId")
    private User owner;

    public File(String path) { }

    public File(String name, String location, User owner) {
        this.name = name;
        this.location = location;
        this.owner = owner;
    }

    public File() {

    }

    public int getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public User getOwner() {
        return owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File[] listFiles() {
        return new File[0];
    }

    public boolean exists() {
        return false;
    }

    public boolean isFile() {
        return false;
    }
}