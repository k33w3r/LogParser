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
        summary = "Download top file upload users as JSON file",
        description = "Exports top file upload statistics as a JSON download file.",
        parameters = {
                @Parameter(
                        name = "count",
                        description = "Number of top users to download",
                        example = "3",
                        schema = @Schema(minimum = "1"),
                        required = false
                )
        },
        responses = {
                @ApiResponse(responseCode = "200", description = "Download generated successfully"),
                @ApiResponse(responseCode = "500", description = "Failed to export file upload summary", content = @Content)
        }
)
public @interface TopFileUploadDownloadSwagger {
}
