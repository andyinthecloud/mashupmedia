package org.mashupmedia.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ResetPasswordPayload {
    @NotBlank(message = "The username should not be empty.")
	private String username;
    private String password;
    private String token;
    private String activationCode;
}
