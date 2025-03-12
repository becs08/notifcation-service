package com.example.notification.service;

import com.example.notification.config.TwilioConfig;
import com.example.notification.model.Notification;
import com.example.notification.model.NotificationType;
import com.example.notification.util.ConnectivityChecker;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service d'envoi de messages via WhatsApp en utilisant Twilio.
 */
@Service
public class TwilioService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(TwilioService.class);

    private final TwilioConfig twilioConfig;
    private final ConnectivityChecker connectivityChecker;

    public TwilioService(TwilioConfig twilioConfig, ConnectivityChecker connectivityChecker) {
        this.twilioConfig = twilioConfig;
        this.connectivityChecker = connectivityChecker;
    }

    @Override
    public Notification sendNotification(String phoneNumber, String message) {
        log.debug("Envoi de message WhatsApp via Twilio à {} : {}", phoneNumber, message);

        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .phoneNumber(phoneNumber)
                .message(message)
                .type(NotificationType.WHATSAPP)
                .sentAt(LocalDateTime.now())
                .build();

        try {
            // Formater le numéro de téléphone au format WhatsApp pour Twilio
            String formattedFromNumber = "whatsapp:" + twilioConfig.getWhatsappFromNumber();
            String formattedToNumber = "whatsapp:" + formatPhoneNumber(phoneNumber);

            // Envoi du message via l'API Twilio
            Message twilioMessage = Message.creator(
                            new PhoneNumber(formattedToNumber),
                            new PhoneNumber(formattedFromNumber),
                            message)
                    .create();

            // Traitement de la réponse de Twilio
            String messageStatus = twilioMessage.getStatus().toString();
            String messageId = twilioMessage.getSid();

            log.debug("Message WhatsApp envoyé via Twilio, ID: {}, Statut: {}", messageId, messageStatus);

            // Mettre à jour le statut de la notification
            switch (messageStatus.toUpperCase()) {
                case "QUEUED":
                case "SENDING":
                case "SENT":
                case "DELIVERED":
                    notification.setStatus("SENT");
                    log.info("Message WhatsApp envoyé avec succès à {}", phoneNumber);
                    break;
                default:
                    notification.setStatus("FAILED");
                    notification.setErrorMessage("Statut Twilio: " + messageStatus);
                    log.error("Échec d'envoi WhatsApp à {}, statut: {}", phoneNumber, messageStatus);
            }

        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            log.error("Exception lors de l'envoi de message WhatsApp à {} : {}",
                    phoneNumber, e.getMessage());
        }

        return notification;
    }

    @Override
    public boolean isAvailable(String phoneNumber) {
        // Utiliser le ConnectivityChecker pour vérifier si le numéro est disponible sur WhatsApp
        try {
            return connectivityChecker.isConnectedToWhatsApp(phoneNumber);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de disponibilité WhatsApp pour {}: {}",
                    phoneNumber, e.getMessage());
            // En cas d'erreur, nous retournons false pour basculer vers le SMS comme fallback
            return false;
        }
    }

    /**
     * Formate un numéro de téléphone pour l'API Twilio WhatsApp.
     * Twilio attend un format international avec le préfixe +.
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+")) {
            return phoneNumber; // Déjà au bon format
        } else if (phoneNumber.startsWith("00")) {
            return "+" + phoneNumber.substring(2);
        } else if (phoneNumber.length() == 9 && phoneNumber.startsWith("7")) {
            // Numéro mobile sénégalais à 9 chiffres
            return "+221" + phoneNumber;
        } else {
            // Par défaut, on suppose un numéro sénégalais
            return "+221" + phoneNumber;
        }
    }
}
