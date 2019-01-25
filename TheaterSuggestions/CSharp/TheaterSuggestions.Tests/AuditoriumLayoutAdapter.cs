using System.Collections.Generic;
using ExternalDependencies.AuditoriumLayoutRepository;
using ExternalDependencies.ReservationsProvider;

namespace SeatsSuggestions.Tests
{
    /// <summary>
    /// Adapt Dtos coming from the external dependencies (ReservationsProvider, AuditoriumLayoutRepository) to AuditoriumLayout instances.
    /// </summary>
    public class AuditoriumLayoutAdapter
    {
        private readonly ReservationsProvider _reservedSeatsRepository;
        private readonly AuditoriumLayoutRepository _auditoriumLayoutRepository;

        public AuditoriumLayoutAdapter(AuditoriumLayoutRepository auditoriumLayoutRepository,
            ReservationsProvider reservationsProvider)
        {
            _auditoriumLayoutRepository = auditoriumLayoutRepository;
            _reservedSeatsRepository = reservationsProvider;
        }

        public AuditoriumLayout GetAuditoriumLayout(string showId)
        {
            return Adapt(_auditoriumLayoutRepository.GetAuditoriumLayoutFor(showId),
                _reservedSeatsRepository.GetReservedSeats(showId));
        }

        private static AuditoriumLayout Adapt(AuditoriumDto auditoriumDto, ReservedSeatsDto reservedSeatsDto)
        {
            var rows = new Dictionary<string, Row>();

            foreach (var rowDto in auditoriumDto.Rows)
            foreach (var seatDto in rowDto.Value)
            {
                var rowName = ExtractRowName(seatDto.Name);
                var number = ExtractNumber(seatDto.Name);
                var priceCategory = ConvertCategory(seatDto.Category);

                var isBookedSeat = reservedSeatsDto.ReservedSeats.Contains(seatDto.Name);

                if (!rows.ContainsKey(rowName))
                {
                    rows[rowName] = new Row();
                }

                rows[rowName].Seats.Add(new Seat(rowName, number, priceCategory,
                    isBookedSeat ? SeatAvailability.Booked : SeatAvailability.Available));
            }

            return new AuditoriumLayout(rows);
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