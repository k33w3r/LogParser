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
        summary = "Download suspicious IP attempts as JSON file",
        description = "Exports detected suspicious login attempts grouped by IP to a downloadable JSON file.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Download of suspicious activity file successful"),
                @ApiResponse(responseCode = "500", description = "Failed to export suspicious activity data", content = @Content)
        }
)
public @interface SuspiciousDownloadSwagger {
}
