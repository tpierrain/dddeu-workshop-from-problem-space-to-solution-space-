using System;
using System.Collections.Generic;
using System.Linq;

namespace SeatsSuggestions.Tests
{
    public class SeatAllocator
    {
        private readonly AuditoriumLayoutAdapter _auditoriumLayoutAdapter;

        public SeatAllocator(AuditoriumLayoutAdapter auditoriumLayoutAdapter)
        {
            _auditoriumLayoutAdapter = auditoriumLayoutAdapter;
        }

        private static SuggestionMade MakeSuggestion(int partyRequested,
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
                            return suggestion;
                    }
                }
            }

            return new SuggestionNotAvailable(partyRequested);
        }

        public SuggestionMade MakeSuggestion(string showId, int partyRequested)
        {
            var suggestion = new Suggestion(partyRequested);

            var theaterLayout = _auditoriumLayoutAdapter.GetAuditoriumLayout(showId);

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
            var auditoriumLayout = _auditoriumLayoutAdapter.GetAuditoriumLayout(showId);

            var suggestions = new Suggestions(showId);

            foreach (var pricingCategory in Enum.GetValues(typeof(PricingCategory)).Cast<PricingCategory>())
            {
                suggestions.AddSuggestion(pricingCategory, suggestion: MakeSuggestion(partyRequested, pricingCategory, auditoriumLayout.Rows));
            }

            return suggestions;
        }
    }

    public class Suggestions
    {
        public string ShowId { get; }

        public Dictionary<PricingCategory, List<Suggestion>> ProposalsPerCategory { get; } =
            new Dictionary<PricingCategory, List<Suggestion>>();

        public Suggestions(string showId)
        {
            ShowId = showId;
        }

        public void AddSuggestion(PricingCategory pricingCategory, Suggestion suggestion)
        {
            if (!ProposalsPerCategory.ContainsKey(pricingCategory))
            {
                ProposalsPerCategory[pricingCategory] = new List<Suggestion>();
            }

            ProposalsPerCategory[pricingCategory].Add(suggestion);
        }
    }
}