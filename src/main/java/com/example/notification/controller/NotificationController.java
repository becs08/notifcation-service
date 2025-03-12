package com.example.notification.controller;

import com.example.notification.dto.NotificationRequest;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.service.NotificationDispatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Contrôleur REST pour gérer les notifications.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationDispatcherService notificationDispatcherService;

    public NotificationController(NotificationDispatcherService notificationDispatcherService) {
        this.notificationDispatcherService = notificationDispatcherService;
    }

    /**
     * Endpoint pour envoyer une notification.
     *
     * @param request La requête contenant les détails de la notification
     * @return La réponse avec les détails de la notification envoyée
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("Réception d'une demande de notification pour {}", request.getPhoneNumber());

        NotificationResponse response = notificationDispatcherService.sendNotification(request);

        HttpStatus status = "SENT".equals(response.getStatus()) ?
                HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(response, status);
    }

    /**
     * Endpoint de santé/vérification du service.
     *
     * @return Un message indiquant que le service est opérationnel
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service de notification opérationnel");
    }

    /**
     * Endpoint pour recevoir les webhooks de Twilio.
     * Ce webhook recevra les mises à jour de statut des messages WhatsApp.
     *
     * @param messageSid L'identifiant du message
     * @param messageStatus Le statut du message
     * @return 200 OK pour confirmer la réception
     */
    @PostMapping("/twilio-webhook")
    public ResponseEntity<String> twilioWebhook(
            @RequestParam(value = "MessageSid", required = false) String messageSid,
            @RequestParam(value = "MessageStatus", required = false) String messageStatus) {

        log.info("Réception d'un webhook Twilio: MessageSid={}, Status={}",
                messageSid, messageStatus);

        // Ici, vous pourriez mettre à jour le statut du message dans votre base de données

        return ResponseEntity.ok("Webhook reçu");
    }
}
