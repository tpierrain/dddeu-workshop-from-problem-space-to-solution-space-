using System.Collections.Generic;
using System.Linq;
using SeatsSuggestions.DeepModel;
using Value;

namespace SeatsSuggestions
{
    public class Row : ValueType<Row>
    {
        public Row(string name, List<Seat> seats)
        {
            Name = name;
            Seats = seats;
        }

        public string Name { get; }
        public List<Seat> Seats { get; }

        public Row AddSeat(Seat seat)
        {
            var updatedList = Seats.Select(s => s == seat ? seat : s).ToList();

            return new Row(Name, updatedList);
        }

        public SeatingOptionSuggested SuggestSeatingOption(SuggestionRequest suggestionRequest)
        {
            var seatingOptionSuggested = new SeatingOptionSuggested(suggestionRequest);

            foreach (var seat in SuggestAdjacentSeatsNearedTheMiddleOfRow(suggestionRequest))
            {
                seatingOptionSuggested.AddSeat(seat);

                if (seatingOptionSuggested.MatchExpectation()) return seatingOptionSuggested;
            }

            return new SeatingOptionNotAvailable(suggestionRequest);
        }

        public Row Allocate(Seat seat)
        {
            var newVersionOfSeats = new List<Seat>();

            foreach (var currentSeat in Seats)
                if (currentSeat.SameSeatLocation(seat))
                    newVersionOfSeats.Add(new Seat(seat.RowName, seat.Number, seat.PricingCategory,
                        SeatAvailability.Allocated));
                else
                    newVersionOfSeats.Add(currentSeat);

            return new Row(seat.RowName, newVersionOfSeats);
        }

        public IEnumerable<Seat> SuggestAdjacentSeatsNearedTheMiddleOfRow(SuggestionRequest suggestionRequest)
        {
            var seatsWithDistances = new OfferingSeatsNearerMiddleOfTheRow(this)
                .SuggestSeatsNearerTheMiddleOfTheRow(suggestionRequest);

            return new OfferingAdjacentSeatsToMembersOfTheSameParty(suggestionRequest)
                .SuggestAdjacentSeats(seatsWithDistances);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { Name, new ListByValue<Seat>(Seats) };
        }
    }
}