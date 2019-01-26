using System;
using System.Collections.Generic;
using System.Linq;

namespace SeatsSuggestions.Tests
{
    public class SeatAllocator
    {
        private readonly AuditoriumSeatingAdapter _auditoriumSeatingAdapter;

        public SeatAllocator(AuditoriumSeatingAdapter auditoriumSeatingAdapter)
        {
            _auditoriumSeatingAdapter = auditoriumSeatingAdapter;
        }

        private static Suggestion MakeSuggestion(int partyRequested,
            PricingCategory pricingCategory, Dictionary<string, Row> rows)
        {
            var suggestion = new Suggestion(partyRequested);

            foreach (var row in rows)
            {
                foreach (var seat in row.Value.Seats)
                {
                    if (seat.IsAvailable() && seat.MatchCategory(pricingCategory))
                    {
                        suggestion.AddSeat(seat);

                        if (suggestion.IsFulFilled)
                        {
                            return suggestion;
                        }
                    }
                }
            }

            return new AllocationNotAvailable(partyRequested);
        }

        public SuggestionMade MakeSuggestion(string showId, int partyRequested)
        {
            var suggestion = new Suggestion(partyRequested);

            var theaterLayout = _auditoriumSeatingAdapter.GetAuditoriumSeating(showId);

            foreach (var row in theaterLayout.Rows)
            foreach (var seat in row.Value.Seats)
            {
                if (seat.IsAvailable())
                {
                    suggestion.AddSeat(seat);

                    if (suggestion.IsFulFilled)
                    {
                        return new SuggestionMade(suggestion.PartyRequested, suggestion.Seats);
                    }
                }
            }

            return new SuggestionNotAvailable(partyRequested);
        }

        public Suggestions MakeSuggestions(string showId, int partyRequested)
        {
            var auditoriumSeating = _auditoriumSeatingAdapter.GetAuditoriumSeating(showId);

            var suggestions = new Suggestions(showId);

            foreach (var pricingCategory in Enum.GetValues(typeof(PricingCategory)).Cast<PricingCategory>())
            {
                suggestions.AddSuggestion(pricingCategory,
                    MakeSuggestion(partyRequested, pricingCategory, auditoriumSeating.Rows));
            }

            return suggestions;
        }
    }
}