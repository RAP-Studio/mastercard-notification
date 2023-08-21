Commercial Event Notifications Client Reference Application  
===================================  

Introduction
------------  

This reference application allows you to execute a series of sample API calls to the Commercial Event Notifications API and to execute all the use cases of the API.

This README will guide you through the process of configuring, building and running the client reference application.

## Author <br>

- [Mastercard API Consultancy & Standards](API_Consultancy_and_Standards@mastercard.com) <br>

### Reference Application Overview  

This application is organized as a series of tests that executes a series of interactions with the Commercial Event Notifications service as follows:  

#### Field Mapping Test
- Get fieldMappings

#### Subscription Test  
- Create subscription without a filter specification
- Create subscription with a filter specification
- Update a subscription
- Update a subscription with a filter specification
- Delete a subscription
- Get all subscriptions
- Get a single subscription

#### Notification Test  
- Get notifications by passing start date and end date
- Get notifications by passing start date and end date and subscription name
- Get notifications by passing start date and end date and subscription name and push delivery status

Software Pre-Requisites
------------------------  

1. Install Java 8
2. Install Maven 3
3. Ensure you have access to the following dependencies:  
    1. Mastercard Developer OAuth Signer v1.2.4 (through the Mastercard Developers portal)
    2. org.openapitools v3.3.4
    3. OKHttp v2.7.5
    4. Junit Jupiter v5.5.2
    5. Assert-j v3.14.0
4. These dependencies are configured in the pom.xml. Maven should download these dependencies automatically.  

## Frameworks Used <br>
- OpenAPI Generator

Mastercard Developers Setup
---------------------------  

1. Create an account at Mastercard Developers - [https://developer.mastercard.com](https://developer.mastercard.com/).  

2. Create a new project and add the `Commercial Event Notifications` API to your project.   

3. A ".p12" keystore file containing sandbox credentials is downloaded automatically. Save this file for later  

4. Take note of the consumer key, keyalias, and keystore password given upon the project creation.

Subscriber Onboarding
---------------------  

- Contact your Mastercard representative to begin the onboarding process. If you do not already have a contact,
use the "Get Help" button on the Support page of the Commercial Event Notifications API Documentation to establish contact.
- To receive push notifications, you must host a server application which will receive the notifications from Mastercard
- A sample server application can be found in the event-notification-sample-java-server folder of this reference application package
- Once onboarding is complete, you can proceed to integrate to the Commercial Event Notifications API.

Project Setup  
---------------------------  
1. Extract the client application from the zip file
2. Import the project into your Integrasted Development Environment (IDE) using the pom file as the base for the project
3. Import the project dependencies
4. Configure the following files with the credentials that you obtained earlier.
    - ~src/test/resources
        * Add the p12 keystore file you obtained earlier
        * Take note of the file path (e.g. ./src/test/resources/MY_CERTIFICATE_NAME_HERE.p12 )
    -  ~src/test/java/tests/utils/Config
        -  In this file there are 6 fields that must be updated.  
            1. BASE_PATH - the base URL of the Commercial Event Notifications API, set to the sandbox environment by default
            2. consumerKey - the key identifier obtained earlier
            3. signingKeyPkcs12FilePath - the file path to the keystore file obtained previously)  
            4. signingKeyAlias - the key alias for the private key in the keystore
            5. signingKeyPassword - the password used to protect the private key
    - Update each of these values with the ones you received from the Mastercard Developers portal  
5. Run a Maven clean install on the project   
    - This will generate the classes required from the OpenAPI Specification file located in ./src/test/resources/schema  
6. Run the first test in the FieldMappingApiTest file to ensure your configuration settings are allowing you to access the API as expected.  
    1. To do this either run the test directly in your IDE or navigate to the root project file and execute "mvn test -Dtest=tests.FieldMappingApiTest#testGetFieldMappingsEndpoint"  
    2. This test verifies the Get FieldMapping API Endpoint is working and retrieves the details of the fieldMappings.  
    3. If the test fails please review the error message provided.
7. Once the first test passes, right click the src package and select run → all tests in your IDE or by executing the command "mvn test".
    - Ensure that mvn delegates the test runs to junit.
8. To run the API tests, run each file as a whole, are there are dependencies between the tests in each file and they will not succeed if run independently.  

## OpenAPI Generator <br>
This application uses OpenAPI Generator to generate the API Calls and object models. It is defined in the pom.xml file
in the project's top-level folder as follows:

            <plugins>
                <plugin>
                    <groupId>org.openapitools</groupId>
                    <artifactId>openapi-generator-maven-plugin</artifactId>
                    <version>${openapi-generator-version}</version>
                    <executions>
                        <execution>
                            <id>Event notifications Rest Client</id>
                            <goals>
                                <goal>generate</goal>
                            </goals>
                            <configuration>
                                <inputSpec>src/test/resources/notification-swagger.yml</inputSpec>
                                <generatorName>java</generatorName>
                                <configOptions>
                                    <sourceFolder>src/gen/java/main</sourceFolder>
                                    <dateLibrary>java8</dateLibrary>
                                </configOptions>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>

For more information on OpenAPI Generator, please consult the official [Github repository](https://github.com/OpenAPITools/openapi-generator)
