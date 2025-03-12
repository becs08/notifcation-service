package com.example.notification.model;

import java.time.LocalDateTime;

/**
 * Entité représentant une notification.
 */
public class Notification {
    private String id;
    private String phoneNumber;
    private String message;
    private NotificationType type;
    private LocalDateTime sentAt;
    private String status;
    private String errorMessage;

    // Constructeurs
    public Notification() {
    }

    public Notification(String id, String phoneNumber, String message, NotificationType type,
                        LocalDateTime sentAt, String status, String errorMessage) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.type = type;
        this.sentAt = sentAt;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    // Pattern Builder (remplace l'annotation @Builder de Lombok)
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    // Getters et Setters explicites (au lieu de dépendre de Lombok)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Classe Builder (remplace l'annotation @Builder de Lombok)
    public static class NotificationBuilder {
        private String id;
        private String phoneNumber;
        private String message;
        private NotificationType type;
        private LocalDateTime sentAt;
        private String status;
        private String errorMessage;

        public NotificationBuilder id(String id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public NotificationBuilder status(String status) {
            this.status = status;
            return this;
        }

        public NotificationBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Notification build() {
            return new Notification(id, phoneNumber, message, type, sentAt, status, errorMessage);
        }
    }
}
