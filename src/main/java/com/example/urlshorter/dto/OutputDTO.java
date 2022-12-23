package com.example.urlshorter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

/**
 * DTO class to output result.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "message",
        "longLink",
        "shortLink"
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutputDTO {
    @JsonProperty("status")
    private Status status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("longLink")
    private String longLink;

    @JsonProperty("shortLink")
    private String shortLink;

    public OutputDTO(String longLink, String shortLink) {
        this.longLink = longLink;
        this.shortLink = shortLink;
    }
}
