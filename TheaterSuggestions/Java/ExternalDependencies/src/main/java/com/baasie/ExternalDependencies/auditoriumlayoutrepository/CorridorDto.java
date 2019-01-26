package com.baasie.ExternalDependencies.auditoriumlayoutrepository;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class CorridorDto {

    @JsonProperty("Number")
    private int number;

    @JsonProperty("InvolvedRowNames")
    private Iterable<String> involvedRowNames;
}
