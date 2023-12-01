package org.mashupmedia.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class CreateUserPayload {
	@Size(min = 3, max = 255, message = "Name should be between 3 and 255 characaters")
	private String name;
    @NotBlank(message = "The username should not be empty.")
	private String username;
    private String password;
    private String token;
    private String activationCode;
}
