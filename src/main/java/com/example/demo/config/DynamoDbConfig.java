package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.Optional;

/**
 * DynamoDb client configuration:
 * - If AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY are present (env), use them.
 * - Otherwise fall back to DefaultCredentialsProvider (IAM role on Render).
 * - Optional endpoint override ONLY used when aws.dynamodb.endpoint is set (dev).
 */
@Configuration
public class DynamoDbConfig {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.dynamodb.endpoint:#{null}}")
    private String endpoint;

    // Optional static credentials — prefer env variables set by Render dashboard
    @Value("${aws.access-key-id:#{null}}")
    private String accessKey;

    @Value("${aws.secret-access-key:#{null}}")
    private String secretKey;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        var builder = DynamoDbClient.builder()
                .region(Region.of(region));

        // If explicit accessKey/secretKey provided in env (only use if both present)
        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey.trim(), secretKey.trim()))
            );
        } else {
            // prefer env/profile/instance-role (DefaultCredentialsProvider)
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        // Optional endpoint override for local/dev (set aws.dynamodb.endpoint only for local)
        if (endpoint != null && !endpoint.isBlank()) {
            // e.g. http://localhost:8000 for local dynamodb
            builder.endpointOverride(URI.create(endpoint.trim()));
            System.out.println("DynamoDB endpoint override enabled: " + endpoint);
        }

        return builder.build();
    }
}
