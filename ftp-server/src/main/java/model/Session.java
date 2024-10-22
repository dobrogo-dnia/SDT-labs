package model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;

    @ManyToOne
    @JoinColumn(unique = true, name="userId")
    private User user;

    @Column
    private LocalDateTime startTime;

    @Column
    private boolean isActive;

    public Session() { }

    public Session(User user) {
        this.user = user;
        this.startTime = LocalDateTime.now();
        this.isActive = true;
    }

    public int getSessionId() {
        return sessionId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}