using System.Collections.Generic;
using System.Linq;
using Value;
using SeatsSuggestions.DeepModel;

namespace SeatsSuggestions
{
    public class Row : ValueType<Row>
    {
        public string Name { get; init; }
        public List<Seat> Seats { get; init; }

        public Row(string name, List<Seat> seats)
        {
            Name = name;
            Seats = seats;
        }

        public Row AddSeat(Seat seat)
        {
            return new Row(Name, new List<Seat>(Seats) { seat });
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
            var seatsWithDistanceFromMiddleOfTheRow =
                new OfferingSeatsNearerMiddleOfTheRow(this).OfferSeatsNearerTheMiddleOfTheRow(suggestionRequest).ToList();

            if (DoNotLookForAdjacentSeatsWhenThePartyContainsOnlyOnePerson(suggestionRequest))
            {
                return seatsWithDistanceFromMiddleOfTheRow.Select(s => s.Seat).ToList();
            }

            // 2. based on seats with distance from the middle of row,
                //    we offer the best group (close to the middle) of adjacent seats
                return new OfferingAdjacentSeatsToMembersOfTheSameParty(suggestionRequest).OfferAdjacentSeats(
                seatsWithDistanceFromMiddleOfTheRow);
        }

        private static bool DoNotLookForAdjacentSeatsWhenThePartyContainsOnlyOnePerson(SuggestionRequest suggestionRequest)
        {
            return suggestionRequest.PartyRequested == 1;
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