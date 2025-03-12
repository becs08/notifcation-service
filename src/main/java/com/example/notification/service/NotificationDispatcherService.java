package com.example.notification.service;

import com.example.notification.dto.NotificationRequest;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.model.Notification;
import com.example.notification.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service qui dispatche les notifications vers le canal approprié (SMS ou WhatsApp)
 * en fonction des préférences de l'utilisateur et de la disponibilité des services.
 */
@Service
public class NotificationDispatcherService {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcherService.class);

    private final SmsService smsService;
    private final TwilioService twilioService;

    public NotificationDispatcherService(SmsService smsService, TwilioService twilioService) {
        this.smsService = smsService;
        this.twilioService = twilioService;
    }

    /**
     * Envoie une notification en choisissant le canal approprié.
     *
     * @param request La requête de notification
     * @return La réponse contenant les détails de la notification envoyée
     */
    public NotificationResponse sendNotification(NotificationRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String message = request.getMessage();
        NotificationType type = request.getType();

        log.info("Traitement de la demande de notification pour {} via {}",
                phoneNumber, type != null ? type : "AUTO");

        Notification notification;

        // Déterminer le canal à utiliser
        if (type == NotificationType.SMS) {
            // L'utilisateur a explicitement demandé un SMS
            notification = smsService.sendNotification(phoneNumber, message);
        } else if (type == NotificationType.WHATSAPP) {
            // L'utilisateur a explicitement demandé WhatsApp
            notification = twilioService.sendNotification(phoneNumber, message);
        } else {
            // Mode AUTO: choisir le canal en fonction de la connectivité
            if (twilioService.isAvailable(phoneNumber)) {
                log.debug("{} est disponible sur WhatsApp", phoneNumber);
                notification = twilioService.sendNotification(phoneNumber, message);
            } else {
                log.debug("{} n'est pas disponible sur WhatsApp, envoi par SMS", phoneNumber);
                notification = smsService.sendNotification(phoneNumber, message);
            }
        }

        // Conversion en DTO de réponse
        return NotificationResponse.builder()
                .id(notification.getId())
                .phoneNumber(notification.getPhoneNumber())
                .type(notification.getType())
                .status(notification.getStatus())
                .message(notification.getMessage())
                .sentAt(notification.getSentAt())
                .errorMessage(notification.getErrorMessage())
                .build();
    }
}
