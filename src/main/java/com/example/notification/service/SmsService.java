package com.example.notification.service;

import com.example.notification.config.OrangeSmsConfig;
import com.example.notification.model.Notification;
import com.example.notification.model.NotificationType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Service d'envoi de SMS utilisant l'API Orange Sénégal.
 */
@Service
public class SmsService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final OrangeSmsConfig orangeSmsConfig;
    private final RestTemplate restTemplate;

    // Cache du token pour éviter de demander un nouveau token à chaque appel
    private String accessToken;
    private LocalDateTime tokenExpiration;

    public SmsService(OrangeSmsConfig orangeSmsConfig,
                      @Qualifier("orangeSmsRestTemplate") RestTemplate restTemplate) {
        this.orangeSmsConfig = orangeSmsConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public Notification sendNotification(String phoneNumber, String message) {
        log.debug("Envoi de SMS à {} : {}", phoneNumber, message);

        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .phoneNumber(phoneNumber)
                .message(message)
                .type(NotificationType.SMS)
                .sentAt(LocalDateTime.now())
                .build();

        try {
            // Obtenir un token valide
            String token = getAccessToken();

            // Formater le numéro de téléphone au format international
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
            log.debug("Numéro formaté: {}", formattedPhoneNumber);

            // Préparer l'URL et les en-têtes
            String url = orangeSmsConfig.getApiUrl() + "/outbound/" +
                    orangeSmsConfig.getSenderAddress() + "/requests";

            // Ajouter des logs pour afficher les valeurs de configuration
            log.debug("Configuration Orange SMS - API URL: {}", orangeSmsConfig.getApiUrl());
            log.debug("Configuration Orange SMS - Sender Address: {}", orangeSmsConfig.getSenderAddress());
            //log.debug("Configuration Orange SMS - Sender Name: {}", orangeSmsConfig.getSenderName());

            // Ajouter un log pour afficher l'URL exacte
            log.debug("URL complète de la requête: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            // Log des headers (attention à ne pas logger le token complet en production)
            log.debug("Headers de la requête: Content-Type={}, Authorization=Bearer ***{}",
                    headers.getContentType(),
                    token != null ? token.substring(0, Math.min(5, token.length())) + "..." : "null");

            // Préparer le corps de la requête avec les modifications
            JSONObject requestBody = new JSONObject();
            JSONObject outboundSMS = new JSONObject();

            // Utiliser "+" devant le numéro formaté
            outboundSMS.put("address", "tel:+" + formattedPhoneNumber);
            log.debug("Format d'adresse utilisé: tel:+{}", formattedPhoneNumber);

            outboundSMS.put("senderAddress", orangeSmsConfig.getSenderAddress());
            outboundSMS.put("senderName", orangeSmsConfig.getSenderName());

            // Limiter la longueur du message si nécessaire
            String limitedMessage = message;
            if (message.length() > 160) {
                limitedMessage = message.substring(0, 160);
                log.debug("Message tronqué à 160 caractères: {}", limitedMessage);
            }

            // Ajouter le message dans une structure plus complexe comme certaines API l'exigent
            JSONObject textMessage = new JSONObject();
            textMessage.put("message", limitedMessage);
            outboundSMS.put("outboundSMSTextMessage", textMessage);

            // L'API peut également s'attendre à avoir le message dans le champ principal
            // Nous gardons donc les deux formats pour être sûrs
            outboundSMS.put("message", limitedMessage);

            requestBody.put("outboundSMSMessageRequest", outboundSMS);

            // Ajouter un log pour afficher le corps de la requête modifié
            log.debug("Corps de la requête modifié: {}", requestBody.toString());

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

            // Envoyer la requête
            log.debug("Envoi de la requête POST à Orange SMS API...");
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class);

            // Traiter la réponse
            log.debug("Réponse reçue avec statut: {}", response.getStatusCode());
            log.debug("Corps de la réponse: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                notification.setStatus("SENT");
                log.info("SMS envoyé avec succès à {}", phoneNumber);
            } else {
                notification.setStatus("FAILED");
                notification.setErrorMessage("Échec avec code " + response.getStatusCodeValue());
                log.error("Échec d'envoi de SMS à {} : {}", phoneNumber, response.getBody());
            }

        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            log.error("Exception lors de l'envoi de SMS à {} : {}", phoneNumber, e.getMessage());
            // Ajouter la trace de la pile pour le débogage
            log.debug("Détails de l'exception:", e);
        }

        return notification;
    }

    @Override
    public boolean isAvailable(String phoneNumber) {
        // Le service SMS est généralement disponible pour tous les numéros valides
        return phoneNumber != null && phoneNumber.matches("^\\+?[0-9]{10,15}$");
    }

    /**
     * Obtient un token d'accès pour l'API Orange.
     * Si un token existe et est valide, il est réutilisé.
     * Sinon, un nouveau token est demandé.
     */
    private String getAccessToken() {
        // Vérifier si le token existe et est valide
        if (accessToken != null && tokenExpiration != null &&
                LocalDateTime.now().isBefore(tokenExpiration)) {
            log.debug("Réutilisation du token existant valide jusqu'à {}", tokenExpiration);
            return accessToken;
        }

        log.debug("Demande d'un nouveau token Orange API");

        String tokenUrl = "https://api.orange.com/oauth/v3/token";
        log.debug("URL de demande de token: {}", tokenUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Basic Auth avec client_id et client_secret
        String auth = orangeSmsConfig.getClientId() + ":" + orangeSmsConfig.getClientSecret();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        log.debug("Client ID utilisé: {}", orangeSmsConfig.getClientId());
        log.debug("Authorization header: Basic ***{}",
                encodedAuth != null ? encodedAuth.substring(0, Math.min(5, encodedAuth.length())) + "..." : "null");

        // Corps de la requête
        String body = "grant_type=client_credentials";
        log.debug("Corps de la requête de token: {}", body);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            log.debug("Envoi de la requête de token...");
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, entity, String.class);

            log.debug("Réponse de token reçue avec statut: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                accessToken = jsonResponse.getString("access_token");

                // Définir l'expiration (généralement 1 heure, mais on prévoit une marge)
                int expiresIn = jsonResponse.getInt("expires_in");
                tokenExpiration = LocalDateTime.now().plusSeconds(expiresIn - 60);

                log.debug("Nouveau token Orange API obtenu, valide jusqu'à {}", tokenExpiration);

                return accessToken;
            } else {
                log.error("Échec d'obtention du token Orange: {}", response.getBody());
                throw new RuntimeException("Impossible d'obtenir un token d'accès Orange");
            }
        } catch (RestClientException e) {
            log.error("Exception lors de la demande de token Orange: {}", e.getMessage());
            throw new RuntimeException("Impossible d'obtenir un token d'accès Orange", e);
        }
    }

    /**
     * Formate un numéro de téléphone au format international.
     * Si le numéro commence déjà par +, il est utilisé tel quel.
     * Sinon, on suppose qu'il s'agit d'un numéro sénégalais et on ajoute +221.
     */
    private String formatPhoneNumber(String phoneNumber) {
        String formatted;

        if (phoneNumber.startsWith("+")) {
            formatted = phoneNumber.substring(1); // Enlever le + car l'API attend le format sans +
        } else if (phoneNumber.startsWith("00")) {
            formatted = phoneNumber.substring(2); // Enlever le 00 international
        } else if (phoneNumber.length() == 9 && phoneNumber.startsWith("7")) {
            // Numéro mobile sénégalais à 9 chiffres
            formatted = "221" + phoneNumber;
        } else {
            // Par défaut, on suppose un numéro sénégalais
            formatted = "221" + phoneNumber;
        }

        log.debug("Formatage du numéro: {} -> {}", phoneNumber, formatted);
        return formatted;
    }
}
