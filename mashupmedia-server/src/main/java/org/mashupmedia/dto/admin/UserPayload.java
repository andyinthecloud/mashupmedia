package org.mashupmedia.dto.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPayload {
    private String username;
	private String name;
	private boolean enabled;
	private boolean editable;
	private boolean system;

    private List<RolePayload> rolePayloads;
    private List<GroupPayload> groupPayloads;

	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;
}
