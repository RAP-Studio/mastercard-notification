package tests;

import com.mastercard.developer.utils.AuthenticationUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.FieldMappingApi;
import org.openapitools.client.model.FieldMapping;
import tests.utils.Config;

import java.security.PrivateKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * This test class allows the user to get fieldmappings. 
 * The initup ial test sets all necessary configurations such as consumer key, P12 file. It is then used to get the fieldmappings.
 **/

public class FieldMappingApiTest implements Config {

    private static ApiClient client;

    @BeforeAll
    public static void before() throws Exception {
        PrivateKey signingKey = AuthenticationUtils.loadSigningKey(signingKeyPkcs12FilePath, signingKeyAlias, signingKeyPassword);
        client = Config.setupApiClient(consumerKey, signingKey, new FieldMappingApiTest.ForceJsonResponseInterceptor());
    }

    /**
     * Test Get Field Mappings
     */
    @Test
    public void testGetFieldMappingsEndpoint() throws Exception {
        FieldMappingApi fieldMappingApi = new FieldMappingApi(client);
        List<FieldMapping> fieldMappingList = fieldMappingApi.fieldmappingsGet();
        System.out.println(fieldMappingList);
        assertThat(fieldMappingList)
                .extracting(FieldMapping::getName, FieldMapping::getDisplayName, FieldMapping::getSubjectType, FieldMapping::getContentType)
                .contains(
                        tuple("messageTypeIndicator", "Message type indicator", SUBJECT_TYPE, CONTENT_TYPE),
                        tuple("inControlOnBehalfServiceResult.vcnAuthorizationCode", "In Control VCN service result code", SUBJECT_TYPE, CONTENT_TYPE),
                        tuple("purchaseRequest.rcnAlias", "Purchase request RCN alias", SUBJECT_TYPE, CONTENT_TYPE),
                        tuple("purchaseRequest.companyId", "Purchase request company id", SUBJECT_TYPE, INTEGER),
                        tuple("purchaseRequest.companyGuid", "Purchase request company GUID", SUBJECT_TYPE, CONTENT_TYPE),
                        tuple("purchaseRequest.issuerGuid", "Purchase request issuer GUID", SUBJECT_TYPE, CONTENT_TYPE));
    }
}