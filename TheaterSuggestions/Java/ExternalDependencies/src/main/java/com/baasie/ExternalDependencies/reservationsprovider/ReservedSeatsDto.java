package com.baasie.ExternalDependencies.reservationsprovider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class ReservedSeatsDto {

    @JsonProperty("ReservedSeats")
    private ImmutableList<String> reservedSeats;
}
