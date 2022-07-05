package org.mashupmedia.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.mashupmedia.dto.share.NameValuePayload;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserPayload {
	private String username;
	private String name;
	private boolean enabled;
	private boolean editable;
	private boolean system;

	private List<NameValuePayload<String>> rolePayloads;
	private List<NameValuePayload<Long>> groupPayloads;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedOn;
}
