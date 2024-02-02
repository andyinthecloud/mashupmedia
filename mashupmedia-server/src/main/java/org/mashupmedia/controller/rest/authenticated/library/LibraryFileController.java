package org.mashupmedia.controller.rest.authenticated.library;

import java.io.File;
import java.util.List;

import org.mashupmedia.dto.library.LibraryFilePayload;
import org.mashupmedia.mapper.library.LibraryFilePayloadMapper;
import org.mashupmedia.service.StorageManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/private/library/file")
@RequiredArgsConstructor
public class LibraryFileController {

	private final StorageManager storageManager;
	private final LibraryFilePayloadMapper libraryFilePayloadMapper;

	// https://spring.io/guides/gs/uploading-files/

	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		storageManager.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return ResponseEntity.ok(Boolean.TRUE);
	}

	@GetMapping(value = "/{libraryId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LibraryFilePayload>> getFiles(@PathVariable long libraryId,
			@RequestParam(required = false) String folderPath) {
		List<File> files = storageManager.getFiles(libraryId, folderPath);
		return ResponseEntity.ok(files.stream()
				.map(libraryFilePayloadMapper::toPayload)
				.toList());
	}

}
