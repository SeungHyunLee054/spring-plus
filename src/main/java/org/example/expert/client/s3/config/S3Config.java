package org.example.expert.client.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		Region region = Region.AP_NORTHEAST_2;

		return S3Client.builder()
			.region(region)
			.credentialsProvider(
				() -> AwsBasicCredentials.create(accessKey, secretKey))
			.build();
	}
}
