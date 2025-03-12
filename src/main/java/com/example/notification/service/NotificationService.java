package com.example.notification.service;

import com.example.notification.model.Notification;

/**
 * Interface pour les services de notification.
 */
public interface NotificationService {

    /**
     * Envoie une notification.
     *
     * @param phoneNumber Le numéro de téléphone du destinataire
     * @param message     Le contenu du message à envoyer
     * @return L'objet Notification avec les détails de l'envoi
     */
    Notification sendNotification(String phoneNumber, String message);

    /**
     * Vérifie si le service est disponible pour le numéro spécifié.
     *
     * @param phoneNumber Le numéro de téléphone à vérifier
     * @return true si le service est disponible, false sinon
     */
    boolean isAvailable(String phoneNumber);
}
