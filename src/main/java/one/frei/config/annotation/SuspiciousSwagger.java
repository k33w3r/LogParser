package one.frei.config.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Retrieve suspicious IP activity",
        description = "Identifies and returns suspicious failed login attempts grouped by IP address.",
        responses = {
                @ApiResponse(responseCode = "200", description = "List of suspicious login attempts successfully retrieved"),
                @ApiResponse(responseCode = "500", description = "Failed to analyze suspicious login patterns", content = @Content)
        }
)
public @interface SuspiciousSwagger {
}
