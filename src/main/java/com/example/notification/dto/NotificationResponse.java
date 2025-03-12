package com.example.notification.dto;

import com.example.notification.model.NotificationType;

import java.time.LocalDateTime;

/**
 * Représente une réponse de notification.
 */
public class NotificationResponse {
    private String id;
    private String phoneNumber;
    private String message;
    private NotificationType type;
    private LocalDateTime sentAt;
    private String status;
    private String errorMessage;

    // Constructeurs
    public NotificationResponse() {
    }

    public NotificationResponse(String id, String phoneNumber, String message, NotificationType type,
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
    public static NotificationResponseBuilder builder() {
        return new NotificationResponseBuilder();
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
    public static class NotificationResponseBuilder {
        private String id;
        private String phoneNumber;
        private String message;
        private NotificationType type;
        private LocalDateTime sentAt;
        private String status;
        private String errorMessage;

        public NotificationResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public NotificationResponseBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public NotificationResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationResponseBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public NotificationResponseBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public NotificationResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public NotificationResponseBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public NotificationResponse build() {
            return new NotificationResponse(id, phoneNumber, message, type, sentAt, status, errorMessage);
        }
    }
}
