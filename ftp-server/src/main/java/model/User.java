package model;

import jakarta.persistence.*;
import userMemento.UserMemento;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false)
    private String homeDirectory;

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private final List<File> files = new ArrayList<>();

    public User() { }

    public User(String username, String password, boolean isAdmin, String homeDirectory) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.homeDirectory = homeDirectory;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setHomeDirectory(String homeDirectory) {
        this.homeDirectory = homeDirectory;
    }

    public UserMemento saveToMemento() {
        return new UserMemento(this.userId, this.username, this.password, this.isAdmin, this.homeDirectory);
    }

    public void restoreFromMemento(UserMemento userMemento) {
        this.userId = userMemento.getUserId();
        this.username = userMemento.getUsername();
        this.password = userMemento.getPassword();
        this.isAdmin = userMemento.isAdmin();
        this.homeDirectory = userMemento.getHomeDirectory();
    }

    @Override
    public String toString() {
        return "User{ userId=" + userId + ", username=" + username + ", isAdmin=" + isAdmin + ", homeDirectory=" + homeDirectory + '}';
    }

}