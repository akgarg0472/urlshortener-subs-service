package com.akgarg.subsservice.notification;

final class NotificationUtils {

    private NotificationUtils() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("java:S1192")
    public static String generateSubscriptionSuccessEmailBody(final NotificationEmailParams params) {
        final var emailBody = new StringBuilder();

        emailBody.append("<div style='font-family:Arial,sans-serif;margin:0;padding:0;background: linear-gradient(to bottom, #f7f8f9, #ffffff)!important;max-width:600px;margin:20px auto;padding:20px;background-color:#fff;border-radius:6px;box-shadow:0 0 10px rgba(0,0,0,0.1);color:#333;text-align:center;'>")
                .append("<img src='").append(params.getLogoUrl()).append("' alt='UrlShortener Logo' style='max-width:20%;height:auto;object-fit:contain;margin-bottom:20px;'/>")
                .append("<br /><h2>Your Subscription is Active!</h2>")
                .append("<p style='font-size:16px;text-align:left'>Dear <strong>").append(params.getName()).append("</strong>,</p>")
                .append("<p style='text-align:left;font-size:16px;'>Thank you for activating your subscription with UrlShortener! We're thrilled to have you onboard with enhanced features and benefits. Below are the details of your subscription:</p>")
                .append("<p style='text-align:left;font-size:16px;'>")
                .append("<strong>Subscription ID:</strong> <span>").append(params.getSubscriptionId()).append("</span><br />")
                .append("<strong>Subscription Pack:</strong> <span>").append(params.getSubscriptionPack()).append("</span><br />")
                .append("<strong>Active Since:</strong> <span>").append(params.getActiveAt()).append("</span><br />")
                .append("<strong>Valid Until:</strong> <span>").append(params.getValidUntil()).append("</span><br />");

        if (!params.getFeatures().isEmpty()) {
            emailBody.append("<strong>Allotted Features:</strong><br />")
                    .append("<ul style='text-align:left;font-size:16px;'>");
            for (final var feature : params.getFeatures()) {
                emailBody.append("<li>").append(feature).append("</li>");
            }
        }

        emailBody.append("</ul></p>")
                .append("<p style='text-align:left;font-size:16px;'>You now have access to all the premium features of UrlShortener, and we are committed to providing you with the best experience. If you have any questions or need support, feel free to reach out to us.</p>")
                .append("<div style='text-align:left;font-size:14px;'>")
                .append("<span>Cheers</span><br/>")
                .append("<span>Akhilesh Garg</span><br/>")
                .append("<span>Lead Developer</span>")
                .append("</div>")
                .append("<a href='").append(params.getDashboardLink()).append("' style='display:inline-block;padding:10px 20px;margin-top:20px;text-decoration:none;background-color:#e91e63;color:#fff;border-radius:5px;'>Go to Dashboard</a>")
                .append("<div style='padding:10px;border-radius:0 0 5px 5px;margin-top:20px;font-size:12px;line-height:18px;'>")
                .append("UrlShortener is a hobby project by Akhilesh Garg<br />&copy; 2025 ÂkHîL, All rights reserved.</div>")
                .append("</div>");

        return emailBody.toString();
    }


}
