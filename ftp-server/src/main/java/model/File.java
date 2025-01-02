package model;

import jakarta.persistence.*;
import visitor.Visitable;
import visitor.Visitor;

@Entity
@Table(name = "files")
public class File implements Visitable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String permissions;

    public File() {}

    public File(String name, String location, User owner, String permissions) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.permissions = "777";
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

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "fileId - " + fileId + "; name - " + name + "; location -" + location + "; owner - " + owner + "; permissions - " + permissions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
