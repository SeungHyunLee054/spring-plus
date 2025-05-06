package org.example.expert.domain.common.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ImageFile, Object> {
	private static final Tika tika = new Tika();
	private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB
	private static final List<String> VALID_MIME_TYPES = List.of(
		"image/jpeg",
		"image/pjpeg",
		"image/png",
		"image/gif",
		"image/bmp",
		"image/x-windows-bmp"
	);

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (Objects.isNull(value)) {
			return true;
		}

		if (value instanceof MultipartFile) {
			return isImage((MultipartFile)value);
		}

		if (value instanceof List<?> list) {
			return list.stream()
				.filter(element -> element instanceof MultipartFile)
				.map(element -> (MultipartFile)element)
				.allMatch(this::isImage);
		}

		return true;
	}

	private boolean isImage(MultipartFile file) {
		if (file.isEmpty()) {
			return false;
		}

		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.contains("..")) {
			return false;
		}

		if (file.getSize() > MAX_FILE_SIZE) {
			return false;
		}

		try (InputStream inputStream = file.getInputStream()) {
			String mimeType = tika.detect(inputStream);

			return VALID_MIME_TYPES.contains(mimeType);
		} catch (IOException e) {
			return false;
		}
	}
}