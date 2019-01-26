using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class SeatAllocation
    {
        private readonly PricingCategory _pricingCategory;
        public List<Seat> Seats { get; } = new List<Seat>();
        public int PartyRequested { get; }

        public SeatAllocation(int partyRequested, PricingCategory priceCategory)
        {
            PartyRequested = partyRequested;
            _pricingCategory = priceCategory;
        }

        public void AddSeat(Seat seat)
        {
            Seats.Add(seat);
        }

        public bool MatchExpectation()
        {
            return Seats.Count == PartyRequested;
        }

        public SuggestionMade ConfirmInterest()
        {
            foreach (var seat in Seats)
            {
                seat.MarkAsAlreadySuggested();
            }

            return new SuggestionMade(PartyRequested, _pricingCategory, Seats);
        }
    }
}