package com.example.urlshorter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

/**
 * DTO class for returning list of violations. See Violation.class
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "requestViolations"
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Violations {
    @JsonProperty("requestViolations")
    private List<Violation> violationList;
}
