using ExternalDependencies.AuditoriumLayoutRepository;
using ExternalDependencies.ReservationsProvider;
using NFluent;
using NUnit.Framework;

namespace SeatsSuggestions.Tests
{
    [TestFixture]
    public class SeatsAllocatorShould
    {
        [SetUp]
        public void SetUp()
        {
            _theaterEventRepository = new AuditoriumLayoutRepository();
            _bookedSeatsRepository = new ReservationsProvider();
        }

        private AuditoriumLayoutRepository _theaterEventRepository;
        private ReservationsProvider _bookedSeatsRepository;

        [Test]
        public void Not_offer_any_seat_when_theater_is_totally_full()
        {
            const string showId = "5";
            const int partyRequested = 1;

            var theaterLayoutProvider = new AuditoriumLayoutProvider(_theaterEventRepository, _bookedSeatsRepository);

            var seatAllocator = new SeatAllocator(theaterLayoutProvider);

            var suggestion = seatAllocator.MakeSuggestion(showId, partyRequested);

            Check.That(suggestion.Seats).HasSize(0);
            Check.That(suggestion).IsInstanceOfType(typeof(SuggestionFailure));
        }

        [Test]
        public void Offer_one_seat_when_theater_contains_only_one_available_seat()
        {
            const string showId = "1";
            const int partyRequested = 1;

            var theaterLayoutProvider = new AuditoriumLayoutProvider(_theaterEventRepository, _bookedSeatsRepository);

            var seatAllocator = new SeatAllocator(theaterLayoutProvider);

            var suggestion = seatAllocator.MakeSuggestion(showId, partyRequested);

            Check.That(suggestion.Seats).HasSize(1);
            Check.That(suggestion.Seats[0].ToString()).IsEqualTo("A3");
        }
    }
}