using ExternalDependencies.AuditoriumLayoutRepository;
using ExternalDependencies.ReservationsProvider;
using NFluent;
using NUnit.Framework;

namespace SeatsSuggestions.Tests
{
    [TestFixture]
    public class SeatsAllocatorShould
    {
        [Test]
        public void Return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved()
        {
            const string showId = "5";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionsMade = seatAllocator.MakeSuggestions(showId, partyRequested);
            Check.That(suggestionsMade.PartyRequested).IsEqualTo(partyRequested);
            Check.That(suggestionsMade.ShowId).IsEqualTo(showId);

            Check.That(suggestionsMade).IsInstanceOf<SuggestionNotAvailable>();
        }

        [Test]
        public void Suggest_one_seat_when_Auditorium_contains_one_available_seat_only()
        {
            const string showId = "1";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionsMade = seatAllocator.MakeSuggestions(showId, partyRequested);

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A3");
        }

        [Test]
        public void Offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity()
        {
            const string eventId = "18";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionsMade = seatAllocator.MakeSuggestions(eventId, partyRequested);
            
            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A3", "A4", "A5");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Second)).ContainsExactly("A1", "A2", "A9");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Third)).ContainsExactly("E1", "E2", "E3");
            
            // BUG!!! => return A6, A7, A8 instead of the expected A1, A2, A3
            Check.That(suggestionsMade.SeatNames(PricingCategory.Mixed)).ContainsExactly("A1", "A2", "A3");
        }
    }
}