package com.baasie.SeatsSuggestionsDomain;

import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.SeatDto;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.ExternalDependencies.reservationsprovider.ReservedSeatsDto;
import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditoriumSeatingAdapter implements IAdaptAuditoriumSeating {

    private final IProvideCurrentReservations reservationsProvider;
    private final IProvideAuditoriumLayouts auditoriumSeatingRepository;


    public AuditoriumSeatingAdapter(IProvideAuditoriumLayouts auditoriumSeatingRepository,
                                    IProvideCurrentReservations reservationsProvider) {
        this.auditoriumSeatingRepository = auditoriumSeatingRepository;
        this.reservationsProvider = reservationsProvider;
    }

    public AuditoriumSeating getAuditoriumSeating(String showId) {
        return adapt(auditoriumSeatingRepository.getAuditoriumSeatingFor(showId),
                reservationsProvider.getReservedSeats(showId));

    }

    private AuditoriumSeating adapt(AuditoriumDto auditoriumDto, ReservedSeatsDto reservedSeatsDto) {

        Map<String, Row> rows = new HashMap<>();

        for (Map.Entry<String, ImmutableList<SeatDto>> rowDto : auditoriumDto.rows().entrySet()) {
            List<Seat> seats = new ArrayList<>();

            rowDto.getValue().forEach(seatDto -> {
                String rowName = rowDto.getKey();
                int number = extractNumber(seatDto.name());
                PricingCategory pricingCategory = convertCategory(seatDto.category());

                boolean isReserved = reservedSeatsDto.reservedSeats().contains(seatDto.name());

                seats.add(new Seat(rowName, number, pricingCategory, isReserved ? SeatAvailability.Reserved : SeatAvailability.Available));
            });

            rows.put(rowDto.getKey(), new Row(rowDto.getKey(), seats));
        }

        return new AuditoriumSeating(rows);
    }

    private static PricingCategory convertCategory(int seatDtoCategory) {
        return PricingCategory.valueOf(seatDtoCategory);
    }

    private static int extractNumber(String name) {
        return Integer.parseUnsignedInt(name.substring(1));
    }
}
