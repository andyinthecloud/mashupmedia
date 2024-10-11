package org.mashupmedia.encode;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.mashupmedia.eums.MediaContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class ProcessQueueItem {
	@EqualsAndHashCode.Include
	private final long mediaItemId;
	@EqualsAndHashCode.Include
	private final MediaContentType mediaContentType;
	private final List<String> commands;
	private final Date createdOn = new Date();

	private Process process;
	private Date processStartedOn;
	private final Path inputPath;
	private final Path outputPath;


}
