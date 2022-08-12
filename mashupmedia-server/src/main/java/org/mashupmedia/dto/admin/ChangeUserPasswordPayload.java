package org.mashupmedia.dto.admin;

import javax.validation.constraints.NotBlank;

public class ChangeUserPasswordPayload {
    @NotBlank
    private String currentPassword;
    
    @NotBlank    
    private String newPassword;
    
    @NotBlank
    private String confirmPassword; 
}
