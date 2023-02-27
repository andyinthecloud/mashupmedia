package org.mashupmedia.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
	@NotBlank(message = "The username should not be empty.")
	private String username;
	@Size(min = 3, max = 255, message = "Name should be between 3 and 255 characaters")
	private String name;
	private boolean enabled;
	private boolean editable;
	private boolean system;
	private boolean administrator;

	private List<NameValuePayload<String>> rolePayloads;
	private List<NameValuePayload<Long>> groupPayloads;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedOn;
}
