package tests.utils;

import com.mastercard.developer.interceptors.OkHttp2OAuth1Interceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.assertj.core.util.Lists;
import org.openapitools.client.ApiClient;

import java.io.IOException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public interface Config {

    // The consumer key, signingKeyPkcs12FilePath below are used to do the authentication/authorization.
    String consumerKey = "fM5jpdwB6ha4Yq7Yd_VPURw8C6bHG_JYhxJGkZQgf26d6545!27422a8c54ad4dedbeae98084d492a080000000000000000";
    String signingKeyAlias = "vcn-sandbox-2";
    String signingKeyPassword = "M@ST3Rc@rd321";
    String signingKeyPkcs12FilePath = "src/test/resources/vcn-sandbox-2-sandbox.p12";

    // The base path below is used to set the API end point which you can run the tests.
    String BASE_PATH = "https://sandbox.api.mastercard.com/commercial-event-notifications";

    // The below are related to the pagination parameters to retrieve subscriptions and notifications
    Integer OFFSET = 1;
    Integer LIMIT = 5;
    String SORT = "name";

    // HTTP Status code
    Integer HTTP_STATUS_NO_CONTENT = 204;

    //Specifications
    String CONTENT_TYPE = "TEXT";
    String SPECIFICATION_TYPE = "FIELD";
    String SPECIFICATION_OPERATOR = "WHERE";
    String INTEGER = "INTEGER";
    String FIELD_OPERATOR = "EQUALS";
    String SUBJECT_TYPE = "PAYMENT_AUTHORIZATION";

    // The start and end dates below are related to the pulling notifications.
    OffsetDateTime startDate = OffsetDateTime.of(LocalDateTime.now().minusWeeks(1), ZoneOffset.UTC);
    OffsetDateTime endDate = OffsetDateTime.now(ZoneOffset.UTC);

    // The subscription name and push status below are related to the pulling notifications.
    List<String> subscriptionNames = Lists.newArrayList("OpenAPITest_SUBSC_NAME_DONT_DELETE", "OpenAPITest_SUBSC_NAME_DONT_DELETE");
    List<String> pushStatus = Lists.newArrayList("DELIVERED");

    // The subscriptions names below used to validate/assert against the notification response.
    String SUBSCRIPTION_NAME = "OpenAPITest_SUBSC_NAME_DONT_DELETE";
    String SUBSCRIPTION_NAME_ANOTHER = "OpenAPITest_SUBSC_NAME_DONT_DELETE";

    /**
     * Returns an ApiClient object that can be used to make API calls.
     * The consumerKey, sigingKey and interceptor arguments must be provided.
     *
     * <p> This methods always return the ApiClient object immediately with
     * provided configurations.
     *
     * @param consumerKey
     * @param signingKey
     * @param interceptor
     * @return ApiClient
     */
    static ApiClient setupApiClient(String consumerKey, PrivateKey signingKey, Interceptor interceptor) {
        ApiClient client = new ApiClient();
        client.setBasePath(BASE_PATH);
        client.setDebugging(true);
        client.getHttpClient().networkInterceptors().add(interceptor);
        client.getHttpClient().networkInterceptors().add(new OkHttp2OAuth1Interceptor(consumerKey, signingKey));
        return client;
    }

    /**
     * The below static class is used to intercept the uri
     */
    static class ForceJsonResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String withJsonFormatUrl = withJsonFormat(originalRequest.uri().toString());
            Request newRequest = originalRequest.newBuilder().url(withJsonFormatUrl).build();
            return chain.proceed(newRequest);
        }

        private String withJsonFormat(String uri) {
            StringBuilder newUri = new StringBuilder(uri);
            newUri.append(uri.contains("?") ? "&" : "?");
            newUri.append("Format=JSON");
            return newUri.toString();
        }
    }

}