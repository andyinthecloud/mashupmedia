package org.mashupmedia.dto.admin;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ChangeUserPasswordPayload {
    private String username;

    @NotBlank(message = "The current password value should not be empty.")
    private String currentPassword;
    
    @NotBlank(message = "The new password value should not be empty.")
    private String newPassword;
    
    @NotBlank(message = "The confirm password value should not be empty.")
    private String confirmPassword; 
}
