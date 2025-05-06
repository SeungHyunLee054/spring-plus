package org.example.expert.client.s3

import org.example.expert.domain.common.exception.ServerException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

/**
 * Amazon S3 파일 업로드 및 관리 서비스
 */
@Component
class S3Service(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    @Value("\${cloud.aws.region.static}") private val region: String,
) {
    companion object {
        private val log = LoggerFactory.getLogger(S3Service::class.java)
        private const val S3_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s"
    }

    /**
     * 파일을 S3에 업로드하고 URL을 반환합니다
     * @throws ServerException S3 업로드 실패시
     * @return S3 이미지 URL
     */
    fun uploadAndGetUrl(multipartFile: MultipartFile): String? {
        val fileName = generateFileName(multipartFile)

        return runCatching {
            uploadFile(multipartFile, fileName)
            generateS3Url(fileName)
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                log.error("S3 업로드 실패 : ${e.message}", e)
                null
            }
        )
    }

    /**
     * S3에서 파일을 삭제합니다. 파일이 없는 경우 무시됩니다.
     * @throws ServerException S3 파일 삭제 실패시
     */
    fun deleteFileIfPresent(profileImageUrl: String?) {
        profileImageUrl.takeIf { !it.isNullOrBlank() }
            ?.let { extractFileName(it) }
            ?.takeIf { isFileExists(it) }
            ?.let { fileName ->
                runCatching {
                    deleteFile(fileName)
                }.onFailure { e ->
                    log.error("S3 파일 삭제 실패 : ${e.message}", e)
                }
            }
    }

    private fun generateFileName(file: MultipartFile): String {
        val extension = file.originalFilename?.substringAfterLast(".")
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return "$uuid.$extension"
    }


    private fun uploadFile(multipartFile: MultipartFile, fileName: String) {
        multipartFile.inputStream.use { inputStream ->
            val request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentLength(multipartFile.size)
                .contentType(multipartFile.contentType)
                .build()

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, multipartFile.size))
        }
    }

    private fun generateS3Url(fileName: String): String =
        S3_URL_FORMAT.format(bucket, region, fileName)

    private fun extractFileName(fileName: String): String =
        fileName.substringAfterLast("/")

    private fun isFileExists(fileName: String): Boolean {
        return runCatching {
            val headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build()

            s3Client.headObject(headObjectRequest)
        }.fold(
            onSuccess = { true },
            onFailure = { false }
        )
    }

    private fun deleteFile(fileName: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build()
        )
    }

}
