package com.baasie.ExternalDependencies;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;

import java.util.concurrent.Future;

public interface IProvideAuditoriumLayouts {

    AuditoriumDto getAuditoriumSeatingFor(String showId);
}
