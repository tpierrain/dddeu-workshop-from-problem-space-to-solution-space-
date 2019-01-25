namespace SeatsSuggestions.Tests
{
    public class SeatAllocator
    {
        private readonly AuditoriumLayoutProvider _auditoriumLayoutProvider;

        public SeatAllocator(AuditoriumLayoutProvider auditoriumLayoutProvider)
        {
            _auditoriumLayoutProvider = auditoriumLayoutProvider;
        }

        public Suggestion MakeSuggestion(string showId, int partyRequested)
        {
            var suggestion = new Suggestion(partyRequested);

            var theaterLayout = _auditoriumLayoutProvider.GetTheater(showId);

            foreach (var row in theaterLayout.Rows)
            foreach (var seat in row.Value.Seats)
            {
                if (seat.IsAvailable)
                {
                    suggestion.AddSeat(seat);

                    if (suggestion.IsFulFilled)
                    {
                        return suggestion;
                    }
                }
            }

            return new SuggestionFailure(partyRequested);
        }
    }
}