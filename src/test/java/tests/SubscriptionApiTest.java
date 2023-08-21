package tests;

import com.mastercard.developer.utils.AuthenticationUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.api.SubscriptionApi;
import org.openapitools.client.model.FieldContent;
import org.openapitools.client.model.SpecificationRequest;
import org.openapitools.client.model.SpecificationResponse;
import org.openapitools.client.model.Subscription;
import org.openapitools.client.model.SubscriptionResponse;
import tests.utils.Config;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * This test class allows the user to get, create, update, delete subscriptions. 
 * The initial test sets up all necessary configurations such as consumer key, P12 file. It is then used to test the subscription.
 *
 * <p>The getSubscriptionRequest(), getSubscriptionRequestWithSpecification(), getSpecificationRequest(), getExpectedContent() return the
 * subscription to create/post subscription for a subscriber.
 **/

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubscriptionApiTest implements Config {

    private static final Subscription.SubjectTypeEnum SUBJECT_TYPE_SUBSCRIPTION = Subscription.SubjectTypeEnum.AUTHORIZATION ;
    private static final Subscription.SubjectTypeEnum SUBJECT_TYPE_SUBSCRIPTION_PAYMENT_UPDATE = Subscription.SubjectTypeEnum.AUTHORIZATION ;
    private static ApiClient client;
    private static final SubscriptionResponse.SubjectTypeEnum SUBJECT_TYPE = SubscriptionResponse.SubjectTypeEnum.AUTHORIZATION;
    private static final SubscriptionResponse.SubjectTypeEnum SUBJECT_TYPE_PAYMENT_UPDATE = SubscriptionResponse.SubjectTypeEnum.AUTHORIZATION;

    @BeforeAll
    public static void before() throws Exception {
        PrivateKey signingKey = AuthenticationUtils.loadSigningKey(signingKeyPkcs12FilePath, signingKeyAlias, signingKeyPassword);
        client = Config.setupApiClient(consumerKey, signingKey, new SubscriptionApiTest.ForceJsonResponseInterceptor());
    }

    /**
     * Test Post subscription without specification
     */
    @Test
    @Order(1)
    public void testPostSubscriptions() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        Subscription subscriptionRequest = getSubscriptionRequest();
        SubscriptionResponse subscription = subscriptionApi.subscriptionsPost(subscriptionRequest);
        assertThat(subscription).isNotNull();
        assertThat(subscription.getName().startsWith(SUBSCRIPTION_NAME)).isTrue();
        assertThat(subscription.getId()).isNotNull();
        assertThat(subscription.getSubjectType()).isEqualTo(SUBJECT_TYPE);
        assertThat(subscription.getSpecifications().isEmpty()).isTrue();
        assertThat(subscription.getActive()).isTrue();
    }
    @Test
    @Order(1)
    public void testPostSubscriptionsForPaymentUpdateSubject() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        Subscription subscriptionRequest = getSubscriptionRequestForPaymentUpdate();
        SubscriptionResponse subscription = subscriptionApi.subscriptionsPost(subscriptionRequest);
        assertThat(subscription).isNotNull();
        assertThat(subscription.getName().startsWith(SUBSCRIPTION_NAME)).isTrue();
        assertThat(subscription.getId()).isNotNull();
        assertThat(subscription.getSubjectType()).isEqualTo(SUBJECT_TYPE_PAYMENT_UPDATE);
        assertThat(subscription.getSpecifications().isEmpty()).isTrue();
        assertThat(subscription.getActive()).isTrue();
    }

    /**
     * Test Post subscription with Specifications
     */
    @Test
    @Order(2)
    public void testPostSubscriptionWithSpecification() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        Subscription subscriptionRequest = getSubscriptionRequestWithSpecification();
        //Post subscription with specification
        SubscriptionResponse subscription = subscriptionApi.subscriptionsPost(subscriptionRequest);
        assertThat(subscription).isNotNull();
        assertThat(subscription.getName().startsWith(SUBSCRIPTION_NAME)).isTrue();
        assertThat(subscription.getId()).isNotNull();
        assertThat(subscription.getSubjectType()).isEqualTo(SUBJECT_TYPE);
        assertThat(subscription.getSpecifications().isEmpty()).isFalse();
        assertThat(subscription.getActive()).isTrue();
        assertThat(subscription.getSpecifications())
                .extracting(SpecificationResponse::getType, SpecificationResponse::getOperator, SpecificationResponse::getFieldOperator, fieldMapping -> fieldMapping.getFieldMapping().getName(), expectedContent -> expectedContent.getExpectedContent().getValue())
                .contains(tuple(SPECIFICATION_TYPE, SPECIFICATION_OPERATOR, FIELD_OPERATOR, "purchaseRequest.rcnAlias", "RcnAliasText"));
    }

    /**
     * Test Get All the subscriptions
     */
    @Test
    @Order(3)
    public void testGetAllSubscriptions() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);

        List<SubscriptionResponse> subscriptions = subscriptionApi.getAllSubscription(OFFSET, LIMIT, SORT);
        assertThat(subscriptions).isNotNull();
        if (subscriptions.size() > 0) {
            assertThat(subscriptions.isEmpty()).isFalse();
        } else {
            assertThat(subscriptions.isEmpty()).isTrue();
        }
    }

    /**
     * Test Update subscription
     */
    @Test
    @Order(4)
    public void testUpdateSubscription() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        //Post subscription
        Subscription subscriptionRequest = getSubscriptionRequest();
        SubscriptionResponse subscription1 = subscriptionApi.subscriptionsPost(subscriptionRequest);
        assertThat(subscription1.getActive()).isTrue();
        subscriptionRequest.setActive(false);
        //Calling the update subscription endpoint
        SubscriptionResponse subscription2 = subscriptionApi.updateSubscription(subscription1.getId(), subscriptionRequest);
        assertThat(subscription2.getName().startsWith(SUBSCRIPTION_NAME)).isTrue();
        assertThat(subscription2.getId()).isNotNull();
        assertThat(subscription2.getSubjectType()).isEqualTo(SUBJECT_TYPE);
        assertThat(subscription2.getSpecifications().isEmpty()).isTrue();
        assertThat(subscription2.getActive()).isFalse();
    }

    /**
     * Test Update subscription by adding a specification
     */
    @Test
    @Order(5)
    public void testUpdateSubscriptionWithSpecification() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        //Post subscription
        Subscription subscriptionRequest = getSubscriptionRequest();
        SubscriptionResponse subscription1 = subscriptionApi.subscriptionsPost(subscriptionRequest);
        //Calling the update subscription with Specification
        subscriptionRequest.setSpecifications(Arrays.asList(getSpecificationRequest()));
        SubscriptionResponse subscription2 = subscriptionApi.updateSubscription(subscription1.getId(), subscriptionRequest);
        assertThat(subscription2.getName().startsWith(SUBSCRIPTION_NAME)).isTrue();
        assertThat(subscription2.getId()).isNotNull();
        assertThat(subscription2.getSubjectType()).isEqualTo(SUBJECT_TYPE);
        assertThat(subscription2.getActive()).isTrue();
        assertThat(subscription2.getSpecifications())
                .extracting(SpecificationResponse::getType, SpecificationResponse::getOperator, SpecificationResponse::getFieldOperator, fieldMapping -> fieldMapping.getFieldMapping().getName(), expectedContent -> expectedContent.getExpectedContent().getValue())
                .contains(tuple(SPECIFICATION_TYPE, SPECIFICATION_OPERATOR, FIELD_OPERATOR, "purchaseRequest.rcnAlias", "RcnAliasText"));
    }

    /**
     * Test Get a subscription by Id
     */
    @Test
    @Order(6)
    public void testGetSubscriptionById() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        //Post subscription
        Subscription subscriptionRequest = getSubscriptionRequest();
        SubscriptionResponse subscription1 = subscriptionApi.subscriptionsPost(subscriptionRequest);
        //Get the subscription posted subscription with GET
        SubscriptionResponse subscription = subscriptionApi.getSubscription(subscription1.getId());
        assertThat(subscription).isNotNull();
        assertThat(subscription.getId()).isEqualTo(subscription1.getId());
        assertThat(subscription.getSubjectType()).isEqualTo(SUBJECT_TYPE);
        assertThat(subscription.getSpecifications().isEmpty()).isTrue();
        assertThat(subscription.getActive()).isTrue();
    }
    @Test
    @Order(6)
    public void testGetSubscriptionByIdForPaymentUpdate() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        //Post subscription
        Subscription subscriptionRequest = getSubscriptionRequestForPaymentUpdate();
        SubscriptionResponse subscription1 = subscriptionApi.subscriptionsPost(subscriptionRequest);
        //Get the subscription posted subscription with GET
        SubscriptionResponse subscription = subscriptionApi.getSubscription(subscription1.getId());
        assertThat(subscription).isNotNull();
        assertThat(subscription.getId()).isEqualTo(subscription1.getId());
        assertThat(subscription.getSubjectType()).isEqualTo(SUBJECT_TYPE_PAYMENT_UPDATE);
        assertThat(subscription.getSpecifications().isEmpty()).isTrue();
        assertThat(subscription.getActive()).isTrue();
    }

    /**
     * Test Delete subscription by Id
     */
    @Test
    @Order(7)
    public void testDeleteSubscriptionById() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        //Post subscription
        Subscription subscriptionRequest = getSubscriptionRequest();
        SubscriptionResponse subscription1 = subscriptionApi.subscriptionsPost(subscriptionRequest);
        //Calling the delete endpoint
        ApiResponse<Void> apiResponse = subscriptionApi.subscriptionsIdDeleteWithHttpInfo(subscription1.getId());
        assertThat(apiResponse.getStatusCode()).isEqualTo(HTTP_STATUS_NO_CONTENT);
    }
    @Test
    @Order(7)
    public void testDeleteSubscriptionByIdForPaymentUpdate() throws Exception {
        SubscriptionApi subscriptionApi = new SubscriptionApi(client);
        //Post subscription
        Subscription subscriptionRequest = getSubscriptionRequestForPaymentUpdate();
        SubscriptionResponse subscription1 = subscriptionApi.subscriptionsPost(subscriptionRequest);
        //Calling the delete endpoint
        ApiResponse<Void> apiResponse = subscriptionApi.subscriptionsIdDeleteWithHttpInfo(subscription1.getId());
        assertThat(apiResponse.getStatusCode()).isEqualTo(HTTP_STATUS_NO_CONTENT);
    }

    private Subscription getSubscriptionRequestWithSpecification() {
        Subscription subscription = getSubscriptionRequest();
        subscription.setSpecifications(Arrays.asList(getSpecificationRequest()));
        return subscription;
    }

    private Subscription getSubscriptionRequest() {
        Subscription subscription = new Subscription();
        subscription.name(SUBSCRIPTION_NAME + new Random().nextInt())
                .subjectType(SUBJECT_TYPE_SUBSCRIPTION)
                .active(true)
                .specifications(new ArrayList<>());
        return subscription;
    }
    private Subscription getSubscriptionRequestForPaymentUpdate() {
        Subscription subscription = new Subscription();
        subscription.name(SUBSCRIPTION_NAME + new Random().nextInt())
                .subjectType(SUBJECT_TYPE_SUBSCRIPTION_PAYMENT_UPDATE)
                .active(true)
                .specifications(new ArrayList<>());
        return subscription;
    }

    private SpecificationRequest getSpecificationRequest() {
        SpecificationRequest specification = new SpecificationRequest();
        return specification
                .type(SPECIFICATION_TYPE)
                .operator(SPECIFICATION_OPERATOR)
                .children(new ArrayList<>())
                .fieldMappingName("purchaseRequest.rcnAlias")
                .fieldOperator(FIELD_OPERATOR)
                .expectedContent(getExpectedContent());
    }

    private FieldContent getExpectedContent() {
        FieldContent fieldContent = new FieldContent();
        fieldContent.contentType(CONTENT_TYPE);
        fieldContent.setValue("RcnAliasText");
        return fieldContent;
    }

}