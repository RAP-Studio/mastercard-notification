package tests;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mastercard.developer.utils.AuthenticationUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.JSON;
import org.openapitools.client.api.NotificationsApi;
import org.openapitools.client.model.CommercialBpsNotificationContent;
import org.openapitools.client.model.Notification;
import org.openapitools.client.model.NotificationContent;
import org.openapitools.client.model.NotificationsWrapper;
import tests.utils.Config;

import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * This test class allows the user to get notifications for a subscriber. 
 * The initial test sets up all necessary configurations such as consumer key, P12 file. It is then used to test the subscription.
 *
 * <p> startDate, endDate parameters are mandatory to retrieve notifications
 * <p> subscription names, push status parameters are optional to retrieve notifications
 * <p> offset, limit parameters are optional to retrieve notification. If it is not provided then it will use default values as mentioned in API document.
 **/

public class NotificationApiTest implements Config {

    private static ApiClient client;
    private static Gson gson;

    @BeforeAll
    public static void before() throws Exception {
        PrivateKey signingKey = AuthenticationUtils.loadSigningKey(signingKeyPkcs12FilePath, signingKeyAlias, signingKeyPassword);
        client = Config.setupApiClient(consumerKey, signingKey, new NotificationApiTest.ForceJsonResponseInterceptor());
        gson = new Gson();
    }

    /**
     * Test Get Notifications with start and end dates
     */
    @Test
    public void testGetNotificationsWithStartAndEndDate() throws Exception {
        List<String> subscriptionNames = Lists.newArrayList();
        List<String> pushStatus = Lists.newArrayList();
        NotificationsApi notificationsApi = new NotificationsApi(client);
        // To test payment update subject type notifications , modify the startDate in Config.java to current date
        NotificationsWrapper notifications = notificationsApi.notificationsGet(startDate, endDate, subscriptionNames, pushStatus, OFFSET, LIMIT);
        if (notifications.getNotifications().size() > 0) {
            assertThat(notifications.getCount()).isGreaterThan(0);
            assertThat(notifications.getOffset()).isEqualTo(1);
            assertThat(notifications.getLimit()).isEqualTo(5);
            assertThat(notifications.getNotifications().size()).isGreaterThan(0);
            assertThat(notifications.getNotifications()).extracting(Notification::getContent).isNotEmpty();
            assertThat(notifications.getNotifications()).extracting(Notification::getSubject).isNotEmpty();

            for(Notification notification : notifications.getNotifications()){
                JsonObject jsonObject = gson.toJsonTree(notification.getContent()).getAsJsonObject();
                if(notification.getSubject().equals("PAYMENT_AUTHORIZATION")){
                    NotificationContent notificationContent = gson.fromJson(jsonObject, NotificationContent.class);
                    assertThat(notificationContent.getMessageTypeIndicator()).isNotEmpty();
                }
                else if(notification.getSubject().equals("PAYMENT_UPDATE")){
                    CommercialBpsNotificationContent commercialBpsNotificationContent = gson.fromJson(jsonObject, CommercialBpsNotificationContent.class);
                    assertThat(commercialBpsNotificationContent.getPurchaseRequestId()).isNotEmpty();
                    assertThat(commercialBpsNotificationContent.getStatus()).isNotEmpty();
                }

            }

        } else {
            assertThat(notifications.getNotifications()).extracting(Notification::getContent).isEmpty();
        }
    }

    /**
     * Test Get Notifications with start and end dates and subscription names.
     */
    @Test
    public void testGetNotificationsWithStartAndEndDateAndSubscriptionNames() throws Exception {
        NotificationsApi notificationsApi = new NotificationsApi(client);
        NotificationsWrapper notifications = notificationsApi.notificationsGet(startDate, endDate, subscriptionNames, pushStatus, OFFSET, LIMIT);
        List<Notification> notificationsList =
                notifications.getNotifications().stream()
                        .map(objects -> new Notification())
                        .collect(Collectors.toList());
        if (notifications.getNotifications().size() > 0) {
            assertThat(notifications.getCount()).isGreaterThan(0);
            assertThat(notifications.getOffset()).isEqualTo(1);
            assertThat(notifications.getLimit()).isEqualTo(5);
            assertThat(notifications.getNotifications().size()).isGreaterThan(0);
            assertThat(notificationsList).extracting("subscriptionName").containsOnly(SUBSCRIPTION_NAME, SUBSCRIPTION_NAME_ANOTHER);
            for(Notification notification : notifications.getNotifications()){
                JsonObject jsonObject = gson.toJsonTree(notification.getContent()).getAsJsonObject();
                if(notification.getSubject().equals("PAYMENT_AUTHORIZATION")){
                    NotificationContent notificationContent = gson.fromJson(jsonObject, NotificationContent.class);
                    assertThat(notificationContent.getMessageTypeIndicator()).isNotEmpty();
                }
                else if(notification.getSubject().equals("PAYMENT_UPDATE")){
                    CommercialBpsNotificationContent commercialBpsNotificationContent = gson.fromJson(jsonObject, CommercialBpsNotificationContent.class);
                    assertThat(commercialBpsNotificationContent.getPurchaseRequestId()).isNotEmpty();
                    assertThat(commercialBpsNotificationContent.getStatus()).isNotEmpty();
                }

            }
        } else {
            assertThat(notificationsList).extracting(Notification::getContent).isEmpty();
        }

    }

    /**
     * Test Get Notifications with start and end dates and subscription names and push status.
     */
    @Test
    public void testGetNotificationsWithStartAndEndDateAndSubscriptionNamesAndPushStatus() throws Exception {
        NotificationsApi notificationsApi = new NotificationsApi(client);
        NotificationsWrapper notifications = notificationsApi.notificationsGet(startDate, endDate, subscriptionNames, pushStatus, OFFSET, LIMIT);
        List<Notification> notificationsList =
                notifications.getNotifications().stream()
                        .map(objects -> new Notification())
                        .collect(Collectors.toList());
        if (notifications.getTotal() > 0) {
            assertThat(notifications.getCount()).isGreaterThan(0);
            assertThat(notifications.getOffset()).isEqualTo(1);
            assertThat(notifications.getLimit()).isEqualTo(5);
            assertThat(notifications.getNotifications().size()).isGreaterThan(0);
            assertThat(notifications.getNotifications()).extracting("subscriptionName").containsOnly(SUBSCRIPTION_NAME, SUBSCRIPTION_NAME_ANOTHER);
            for(Notification notification : notifications.getNotifications()){
                JsonObject jsonObject = gson.toJsonTree(notification.getContent()).getAsJsonObject();
                if(notification.getSubject().equals("PAYMENT_AUTHORIZATION")){
                    NotificationContent notificationContent = gson.fromJson(jsonObject, NotificationContent.class);
                    assertThat(notificationContent.getMessageTypeIndicator()).isNotEmpty();
                }
                else if(notification.getSubject().equals("PAYMENT_UPDATE")){
                    CommercialBpsNotificationContent commercialBpsNotificationContent = gson.fromJson(jsonObject, CommercialBpsNotificationContent.class);
                    assertThat(commercialBpsNotificationContent.getPurchaseRequestId()).isNotEmpty();
                    assertThat(commercialBpsNotificationContent.getStatus()).isNotEmpty();
                }

            }
        } else {
            assertThat(notificationsList).extracting(Notification::getContent).isEmpty();
        }

    }

}