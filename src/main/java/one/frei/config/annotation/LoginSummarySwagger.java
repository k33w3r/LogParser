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
        summary = "Retrieve all user login summaries",
        description = "Fetches aggregated login statistics per user from processed system logs.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved login summaries"),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        }
)
public @interface LoginSummarySwagger {
}
