package com.example.translateconnector.utils;

import com.imoktranslator.model.OrderNotificationModel;

/**
 * Created by tontn on 3/28/18.
 */

public class Validator {
    public static boolean validEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean validPhone(String phone) {
        if (phone == null) {
            return false;
        } else {
            return phone.matches("[0-9]+");
        }
    }

    public static boolean validNotificationInfo(OrderNotificationModel notification) {
        boolean isValid = false;

        if (notification.isNotificationForWorker()) {
            if (notification.getOrder() != null &&
                    notification.getCreatedAt() != null &&
                    notification.getStatus() != null) {
                isValid = true;
            }
        } else if (notification.isNotificationForOwner()) {
            if (notification.getSender() != null &&
                    notification.getType() != null &&
                    notification.getOrder() != null &&
                    notification.getCreatedAt() != null &&
                    notification.getStatus() != null) {
                isValid = true;
            }
        } else if (notification.getType() == OrderNotificationModel.CANCELLED) {
            if (notification.getOrder() != null &&
                    notification.getCreatedAt() != null &&
                    notification.getStatus() != null) {
                isValid = true;
            }
        }

        return isValid;
    }
}
