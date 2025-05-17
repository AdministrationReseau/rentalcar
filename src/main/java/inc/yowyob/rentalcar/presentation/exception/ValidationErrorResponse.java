package inc.yowyob.rentalcar.presentation.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(int status, String error, String message, LocalDateTime timestamp, Map<String, String> validationErrors) {
        super(status, error, message, timestamp);
        this.validationErrors = validationErrors;
    }
}
