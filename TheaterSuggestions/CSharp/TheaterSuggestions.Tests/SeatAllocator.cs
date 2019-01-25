namespace SeatsSuggestions.Tests
{
    public class SeatAllocator
    {
        private readonly AuditoriumLayoutAdapter _auditoriumLayoutAdapter;

        public SeatAllocator(AuditoriumLayoutAdapter auditoriumLayoutAdapter)
        {
            _auditoriumLayoutAdapter = auditoriumLayoutAdapter;
        }

        public SuggestionMade MakeSuggestion(string showId, int partyRequested)
        {
            var suggestion = new Suggestion(partyRequested);

            var theaterLayout = _auditoriumLayoutAdapter.GetAuditoriumLayout(showId);

            foreach (var row in theaterLayout.Rows)
            foreach (var seat in row.Value.Seats)
            {
                if (seat.IsAvailable)
                {
                    suggestion.AddSeat(seat);

                    if (suggestion.IsFulFilled)
                    {
                        return new SuggestionMade(suggestion.PartyRequested, suggestion.Seats);
                    }
                }
            }

            return new SeatsNotAvailable(partyRequested);
        }
    }
}