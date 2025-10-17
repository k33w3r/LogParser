package one.frei.config.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Retrieve top users by file uploads",
        description = "Returns a list of users ranked by the number of files uploaded. The count parameter controls how many top users to return.",
        parameters = {
                @Parameter(
                        name = "count",
                        description = "Number of top users to retrieve",
                        example = "3",
                        schema = @Schema(minimum = "1"),
                        required = false
                )
        },
        responses = {
                @ApiResponse(responseCode = "200", description = "List of top file upload users successfully retrieved"),
                @ApiResponse(responseCode = "400", description = "Invalid request parameter (count must be > 0)", content = @Content),
                @ApiResponse(responseCode = "500", description = "Error processing upload summary", content = @Content)
        }
)
public @interface TopFileUploadSwagger {
}
