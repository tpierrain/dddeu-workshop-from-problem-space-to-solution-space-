package com.baasie.ExternalDependencies.auditoriumlayoutrepository;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class AuditoriumDto {

    @JsonProperty("Rows")
    private ImmutableMap<String, ImmutableList<SeatDto>> rows;

    @JsonProperty("Corridors")
    private ImmutableList<CorridorDto> corridors;
}
