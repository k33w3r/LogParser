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
        summary = "Download login summaries as a JSON file",
        description = "Retrieves login summary information and provides it as a downloadable JSON file with proper headers.",
        responses = {
                @ApiResponse(responseCode = "200", description = "File successfully generated and returned"),
                @ApiResponse(responseCode = "500", description = "Unable to process or export summary file", content = @Content)
        }
)
public @interface LoginSummaryDownloadSwagger {
}
