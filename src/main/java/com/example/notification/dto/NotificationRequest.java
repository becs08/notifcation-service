package com.example.notification.dto;

import com.example.notification.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Représente une demande de notification.
 */
public class NotificationRequest {

    @NotBlank(message = "Le numero de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Format de numero invalide")
    private String phoneNumber;

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    private NotificationType type = NotificationType.AUTO; // Par défaut, le système détermine automatiquement

    // Constructeurs
    public NotificationRequest() {
    }

    public NotificationRequest(String phoneNumber, String message, NotificationType type) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.type = type;
    }

    // Getters et Setters explicites (au lieu de dépendre de Lombok)
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
}
