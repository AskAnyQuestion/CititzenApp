package com.example.application.data;

public class NotificationData {
    private int userId;
    private int notificationId;

    public NotificationData(int userId, int notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }

    public NotificationData(String userId, String notificationId) {
        this.userId = Integer.parseInt(userId);
        this.notificationId = Integer.parseInt(notificationId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
