package one.frei.config.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(
        name = "Log Analysis API",
        description = "Endpoints for analyzing, summarizing, and exporting log data including login summaries, file upload activity, and suspicious IP detections."
)
public @interface LogAnalysisSwagger {
}
