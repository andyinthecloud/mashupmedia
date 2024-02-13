package org.mashupmedia.controller.rest.authenticated.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mashupmedia.dto.library.BreadcrumbPayload;
import org.mashupmedia.dto.library.LibraryDeleteFilePayload;
import org.mashupmedia.dto.library.LibraryFilePayload;
import org.mashupmedia.dto.library.LibraryFilesPayload;
import org.mashupmedia.dto.library.LibraryRenameFilePayload;
import org.mashupmedia.mapper.library.LibraryFilePayloadMapper;
import org.mashupmedia.model.library.Library;
import org.mashupmedia.model.location.Location;
import org.mashupmedia.service.LibraryManager;
import org.mashupmedia.service.StorageManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	private final LibraryManager libraryManager;

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
	public ResponseEntity<LibraryFilesPayload> getFiles(@PathVariable long libraryId,
			@RequestParam(required = false) String folderPath) {

		List<LibraryFilePayload> files = storageManager.getFiles(libraryId, folderPath)
				.stream()
				.map(libraryFilePayloadMapper::toPayload)
				.toList();

		List<BreadcrumbPayload> breadcrumbPayloads = getBreadcrumbs(libraryId, folderPath);

		return ResponseEntity.ok(LibraryFilesPayload.builder()
				.breadcrumbPayloads(breadcrumbPayloads)
				.libraryFilePayloads(files)
				.build());
	}

	private List<BreadcrumbPayload> getBreadcrumbs(long libraryId, String folderPath) {
		Library library = libraryManager.getLibrary(libraryId);
		Location location = library.getLocation();
		String libraryPath = location.getPath();
		File libraryFolder = new File(libraryPath);

		List<BreadcrumbPayload> breadcrumbPayloads = new ArrayList<>();

		breadcrumbPayloads.add(BreadcrumbPayload.builder()
		.name(libraryFolder.getName())
		.path(libraryFolder.getAbsolutePath())
		.build());

		if (StringUtils.isBlank(folderPath)) {
			return breadcrumbPayloads;
		}

		if (libraryFolder.isFile()) {
			return breadcrumbPayloads;
		}

		File folder = new File(folderPath);
		if (folder.isFile()) {
			return breadcrumbPayloads;
		}

		if (!folder.getAbsolutePath().startsWith(libraryFolder.getAbsolutePath())) {
			return breadcrumbPayloads;
		}

		List<BreadcrumbPayload> folderBreadcrumbPayloads = new ArrayList<>();
		File breadcrumbFile = new File(folderPath);
		while (!libraryFolder.getAbsolutePath().equals(breadcrumbFile.getAbsolutePath())) {
			folderBreadcrumbPayloads.add(BreadcrumbPayload.builder()
					.name(breadcrumbFile.getName())
					.path(breadcrumbFile.getAbsolutePath())
					.build());
			breadcrumbFile = breadcrumbFile.getParentFile();
		}

		Collections.reverse(folderBreadcrumbPayloads);
		breadcrumbPayloads.addAll(folderBreadcrumbPayloads);
		return breadcrumbPayloads;
	}


	@PutMapping(value = "/rename", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> renameFile(@RequestBody LibraryRenameFilePayload libraryRenameFilePayload) {
		return ResponseEntity.ok().body(
			storageManager.rename(libraryRenameFilePayload.getLibraryId(), libraryRenameFilePayload.getPath(), libraryRenameFilePayload.getName())
		);
	}

	@DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteFile(@RequestBody LibraryDeleteFilePayload libraryDeleteFilePayload) {
		return ResponseEntity.ok().body(
			storageManager.delete(libraryDeleteFilePayload.getLibraryId(), libraryDeleteFilePayload.getPath())
		);
	}
}
