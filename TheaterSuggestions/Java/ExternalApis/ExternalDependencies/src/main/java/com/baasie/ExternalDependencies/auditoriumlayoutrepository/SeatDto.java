package com.baasie.ExternalDependencies.auditoriumlayoutrepository;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class SeatDto {

    @JsonProperty("Name")
    public String name;

    @JsonProperty("Category")
    public int category;
}
