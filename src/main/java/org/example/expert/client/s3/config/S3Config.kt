package org.example.expert.client.s3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(
    @Value("\${cloud.aws.credentials.access-key}") private val accessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}") private val secretKey: String,
    @Value("\${cloud.aws.region.static}") private val region: String
) {

    @Bean
    fun s3Client(): S3Client? {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider({ AwsBasicCredentials.create(accessKey, secretKey) })
            .build()
    }
}
