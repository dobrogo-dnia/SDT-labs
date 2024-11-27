package userMemento;

public class UserMemento {
    private final int userId;
    private final String username;
    private final String password;
    private final boolean isAdmin;
    private final String homeDirectory;

    public UserMemento(int userId, String username, String password, boolean isAdmin, String homeDirectory) {
        this.userId = userId;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }
}