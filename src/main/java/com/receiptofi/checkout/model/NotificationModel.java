package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 3/25/15 7:19 PM
 */
public class NotificationModel {
    private String id;
    private String message;
    private boolean visible;
    private String notificationType;
    private String referenceId;
    private String created;
    private String updated;

    public NotificationModel(String id, String message, boolean visible, String notificationType, String referenceId, String created, String updated) {
        this.id = id;
        this.message = message;
        this.visible = visible;
        this.notificationType = notificationType;
        this.referenceId = referenceId;
        this.created = created;
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }
}
