using System.Collections.Generic;
using System.Linq;
using Value;
using SeatsSuggestions.DeepModel;

namespace SeatsSuggestions
{
    public class Row : ValueType<Row>
    {
        public string Name { get; }
        public List<Seat> Seats { get; }

        public Row(string name, List<Seat> seats)
        {
            Name = name;
            Seats = seats;
        }

        public Row AddSeat(Seat seat)
        {
            var updatedList = Seats.Select(s => s == seat ? seat : s).ToList();

            return new Row(Name, updatedList);
        }

        public SeatingOptionSuggested SuggestSeatingOption(SuggestionRequest suggestionRequest)
        {
            var seatingOptionSuggested = new SeatingOptionSuggested(suggestionRequest);

            foreach (var seat in OfferAdjacentSeatsNearerTheMiddleOfRow(suggestionRequest))
            {
                seatingOptionSuggested.AddSeat(seat);

                if (seatingOptionSuggested.MatchExpectation())
                {
                    return seatingOptionSuggested;
                }
            }

            return new SeatingOptionNotAvailable(suggestionRequest);
        }

        public IEnumerable<Seat> OfferAdjacentSeatsNearerTheMiddleOfRow(SuggestionRequest suggestionRequest)
        {
            // 1. offer seats from the middle of the row
            var seatsWithDistanceFromMiddleOfTheRow = new OfferingSeatsNearerMiddleOfTheRow(this).OfferSeatsNearerTheMiddleOfTheRow(suggestionRequest);

            return seatsWithDistanceFromMiddleOfTheRow.Select(seatWithTheDistanceFromTheMiddleOfTheRow => seatWithTheDistanceFromTheMiddleOfTheRow.Seat).ToList();
        }

        public Row Allocate(Seat seat)
        {
            var newVersionOfSeats = new List<Seat>();

            foreach (var currentSeat in Seats)
            {
                if (currentSeat.SameSeatLocation(seat))
                {
                    newVersionOfSeats.Add(new Seat(seat.RowName, seat.Number, seat.PricingCategory,
                        SeatAvailability.Allocated));
                }
                else
                {
                    newVersionOfSeats.Add(currentSeat);
                }
            }

            return new Row(seat.RowName, newVersionOfSeats);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] {Name, new ListByValue<Seat>(Seats)};
        }
    }
}