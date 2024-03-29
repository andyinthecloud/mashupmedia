package org.mashupmedia.dto.share;

import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorPayload {
    private List<ObjectError> objectErrors;
    private List<FieldError> fieldErrors;
}
