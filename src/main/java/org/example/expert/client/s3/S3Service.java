package org.example.expert.client.s3;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3Service {
	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String uploadAndGetUrl(MultipartFile multipartFile) {
		String extension = Objects.requireNonNull(multipartFile.getOriginalFilename())
			.substring(multipartFile.getOriginalFilename().lastIndexOf("."));
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String fileName = uuid + extension;

		try (InputStream inputStream = multipartFile.getInputStream()) {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.contentLength(multipartFile.getSize())
				.contentType(multipartFile.getContentType())
				.build();

			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));

			return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, fileName);
		} catch (Exception e) {
			throw new RuntimeException("S3 업로드 실패", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteFileIfPresent(String profileImageUrl) {
		if (profileImageUrl == null || profileImageUrl.isBlank()) {
			return;
		}

		String fileName = profileImageUrl.substring(profileImageUrl.lastIndexOf("/") + 1);

		try {
			HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.build();

			s3Client.headObject(headObjectRequest);
		} catch (NoSuchKeyException e) {
			return;
		}

		try {
			s3Client.deleteObject(
				DeleteObjectRequest.builder()
					.bucket(bucket)
					.key(fileName)
					.build());
		} catch (Exception e) {
			throw new RuntimeException("S3 파일 삭제 실패", e);

		}
	}
}
