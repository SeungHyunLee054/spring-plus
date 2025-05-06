package org.example.expert.domain.common.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.apache.tika.Tika
import org.springframework.web.multipart.MultipartFile

class ImageFileValidator : ConstraintValidator<ImageFile, Any> {
    override fun isValid(value: Any, context: ConstraintValidatorContext): Boolean =
        when (value) {
            is MultipartFile -> isImage(value)
            is List<*> -> value.filterIsInstance<MultipartFile>()
                .all { isImage(it) }

            else -> true
        }

    private fun isImage(file: MultipartFile): Boolean {
        if (file.isEmpty) {
            return false
        }

        val originalFilename = file.originalFilename ?: return false
        if (originalFilename.contains("..")) {
            return false
        }

        if (file.size > MAX_FILE_SIZE) {
            return false
        }

        return runCatching {
            file.inputStream.use { inputStream ->
                tika.detect(inputStream) in VALID_MIME_TYPES
            }
        }.getOrDefault(false)
    }

    companion object {
        private val tika = Tika()
        private const val MAX_FILE_SIZE = (1024 * 1024) // 1MB
        private val VALID_MIME_TYPES = setOf(
            "image/jpeg",
            "image/pjpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/x-windows-bmp"
        )
    }
}