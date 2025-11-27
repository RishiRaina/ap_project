package edu.univ.erp.domain;

import java.sql.Timestamp;

public class Notification {

    private int id;
    private Integer targetUserId;
    private String targetRole;
    private String title;
    private String message;
    private Timestamp createdAt;

    public Notification() {}


    public Notification(Integer targetUserId,
                        String targetRole,
                        String title,
                        String message) {
        this.targetUserId = targetUserId;
        this.targetRole = targetRole;
        this.title = title;
        this.message = message;
    }



    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Integer targetUserId) { this.targetUserId = targetUserId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
