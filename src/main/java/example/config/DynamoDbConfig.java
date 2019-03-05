package example.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.ConversionSchemas;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import example.cars.Car;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@EnableDynamoDBRepositories(
    dynamoDBMapperConfigRef = "dynamoDBMapperConfig",
    basePackageClasses = {Car.class}
)
public class DynamoDbConfig {

  @Value("${dynamodb.region}")
  private String amazonAWSRegion;

  @Value("${dynamodb.accesskey}")
  private String amazonAWSAccessKey;

  @Value("${dynamodb.secretkey}")
  private String amazonAWSSecretKey;

  @Value("${dynamodb.table-prefix-env}")
  private String amazonDynamoDBTablePrefixEnv;

  @Bean
  public AWSCredentials amazonAWSCredentials() {
    return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDB(AWSCredentials awsCredentials) {

    return AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.fromName(amazonAWSRegion))
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .build();

  }

  @Bean
  public TableNameOverride tableNameOverride() {

    String tablePrefix = String.format("%s.", amazonDynamoDBTablePrefixEnv);

    return TableNameOverride.withTableNamePrefix(tablePrefix);

  }

  @Bean
//  @Primary
  public DynamoDBMapperConfig dynamoDBMapperConfig(TableNameOverride tableNameOverrider) {

    DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();

    builder.setTableNameOverride(tableNameOverrider);
    builder.setConversionSchema(ConversionSchemas.V2);

    return new DynamoDBMapperConfig(DynamoDBMapperConfig.DEFAULT, builder.build());
  }

  @Bean
//  @Primary
  public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB dynamoDB, DynamoDBMapperConfig config) {
    return new DynamoDBMapper(dynamoDB, config);
  }

}
