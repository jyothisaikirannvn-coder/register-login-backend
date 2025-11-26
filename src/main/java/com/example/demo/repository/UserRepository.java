package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public UserRepository(DynamoDbClient dynamoDbClient,
                          @Value("${aws.dynamodb.user-table-name}") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public void save(User user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("userId", AttributeValue.fromS(user.getUserId()));
        item.put("email", AttributeValue.fromS(user.getEmail()));
        item.put("username", AttributeValue.fromS(user.getUsername()));
        item.put("passwordHash", AttributeValue.fromS(user.getPasswordHash()));
        item.put("role", AttributeValue.fromS(user.getRole()));  // New field
        item.put("firstName", AttributeValue.fromS(user.getFirstName()));
        item.put("lastName", AttributeValue.fromS(user.getLastName()));
        item.put("phone", AttributeValue.fromS(user.getPhone()));
        item.put("dateOfBirth", AttributeValue.fromS(user.getDateOfBirth()));
        item.put("riskAppetite", AttributeValue.fromS(user.getRiskAppetite()));
        item.put("experience", AttributeValue.fromS(user.getExperience()));
        item.put("investmentGoal", AttributeValue.fromS(user.getInvestmentGoal()));
        item.put("createdAt", AttributeValue.fromS(user.getCreatedAt()));
        item.put("updatedAt", AttributeValue.fromS(user.getUpdatedAt()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    // Existing methods...

    public boolean existsByEmail(String email) {
        ScanRequest request = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression("email = :email")
                .expressionAttributeValues(Map.of(":email", AttributeValue.fromS(email)))
                .limit(1)
                .build();

        return !dynamoDbClient.scan(request).items().isEmpty();
    }

    public boolean existsByUsername(String username) {
        ScanRequest request = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression("username = :username")
                .expressionAttributeValues(Map.of(":username", AttributeValue.fromS(username)))
                .limit(1)
                .build();

        return !dynamoDbClient.scan(request).items().isEmpty();
    }

    // New method for login
    public Optional<User> findByEmail(String email) {
        ScanRequest request = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression("email = :email")
                .expressionAttributeValues(Map.of(":email", AttributeValue.fromS(email)))
                .limit(1)
                .build();

        ScanResponse response = dynamoDbClient.scan(request);
        if (response.items().isEmpty()) {
            return Optional.empty();
        }

        Map<String, AttributeValue> item = response.items().get(0);
        User user = new User();
        user.setUserId(item.get("userId").s());
        user.setEmail(item.get("email").s());
        user.setUsername(item.get("username").s());
        user.setPasswordHash(item.get("passwordHash").s());
        user.setRole(item.get("role").s());
        user.setFirstName(item.get("firstName").s());
        user.setLastName(item.get("lastName").s());
        user.setPhone(item.get("phone").s());
        user.setDateOfBirth(item.get("dateOfBirth").s());
        user.setRiskAppetite(item.get("riskAppetite").s());
        user.setExperience(item.get("experience").s());
        user.setInvestmentGoal(item.get("investmentGoal").s());
        user.setCreatedAt(item.get("createdAt").s());
        user.setUpdatedAt(item.get("updatedAt").s());

        return Optional.of(user);
    }
}