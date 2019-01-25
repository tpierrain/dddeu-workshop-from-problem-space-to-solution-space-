using System.Collections.Generic;
using ExternalDependencies.AuditoriumLayoutRepository;
using ExternalDependencies.ReservationsProvider;

namespace SeatsSuggestions.Tests
{
    public class AuditoriumLayoutProvider
    {
        private readonly ReservationsProvider _bookedSeatsRepository;
        private readonly AuditoriumLayoutRepository _theaterEventRepository;

        public AuditoriumLayoutProvider(AuditoriumLayoutRepository auditoriumLayoutRepository,
            ReservationsProvider reservationsProvider)
        {
            _theaterEventRepository = auditoriumLayoutRepository;
            _bookedSeatsRepository = reservationsProvider;
        }

        public TheaterLayout GetTheater(string showId)
        {
            return AdaptTheaterDto(_theaterEventRepository.GetAuditoriumLayoutFor(showId),
                _bookedSeatsRepository.GetBookedSeats(showId));
        }

        private static TheaterLayout AdaptTheaterDto(AuditoriumDto auditoriumDto, ReservedSeatsDto reservedSeatsDto)
        {
            var rows = new Dictionary<string, Row>();

            foreach (var rowDto in auditoriumDto.Rows)
            foreach (var seatDto in rowDto.Value)
            {
                var rowName = ExtractRowName(seatDto.Name);
                var number = ExtractNumber(seatDto.Name);
                var priceCategory = ConvertCategory(seatDto.Category);

                var isBookedSeat = reservedSeatsDto.BookedSeats.Contains(seatDto.Name);

                if (!rows.ContainsKey(rowName))
                {
                    rows[rowName] = new Row();
                }

                rows[rowName].Seats.Add(new Seat(rowName, number, priceCategory,
                    isBookedSeat ? SeatAvailability.Booked : SeatAvailability.Available));
            }

            return new TheaterLayout(rows);
        }

        private static PricingCategory ConvertCategory(int seatDtoCategory)
        {
            return (PricingCategory) seatDtoCategory;
        }

        private static uint ExtractNumber(string name)
        {
            return uint.Parse(name.Substring(1));
        }

        private static string ExtractRowName(string name)
        {
            return name[0].ToString();
        }
    }
}