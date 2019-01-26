using System.Linq;
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

            var suggestionMade = seatAllocator.MakeSuggestion(showId, partyRequested);

            Check.That(suggestionMade.SuggestedSeats).HasSize(0);
            Check.That(suggestionMade).IsInstanceOf<SuggestionNotAvailable>();
        }

        [Test]
        public void Suggest_one_seat_when_Auditorium_contains_one_available_seat_only()
        {
            const string showId = "1";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionMade = seatAllocator.MakeSuggestion(showId, partyRequested);

            Check.That(suggestionMade.SuggestedSeats).HasSize(1);
            Check.That(suggestionMade.SuggestedSeats[0].ToString()).IsEqualTo("A3");
        }

        [Test]
        public void Offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity()
        {
            const string eventId = "18";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestions = seatAllocator.MakeSuggestions(eventId, partyRequested);

            var suggestion = suggestions.ProposalsPerCategory[PricingCategory.First].First();
            Check.That(suggestion.Seats).HasSize(1);
            Check.That(suggestion.Seats[0].ToString()).IsEqualTo("A3");

            suggestion = suggestions.ProposalsPerCategory[PricingCategory.Second].First();
            Check.That(suggestion.Seats).HasSize(1);
            Check.That(suggestion.Seats[0].ToString()).IsEqualTo("A1");

            suggestion = suggestions.ProposalsPerCategory[PricingCategory.Third].First();
            Check.That(suggestion.Seats).HasSize(1);
            Check.That(suggestion.Seats[0].ToString()).IsEqualTo("E1");

            // BUG!!! => return A2 instead of A1 (as expected)
            suggestion = suggestions.ProposalsPerCategory[PricingCategory.Mixed].First();
            Check.That(suggestion.Seats).HasSize(1);
            Check.That(suggestion.Seats[0].ToString()).IsEqualTo("A1");
        }
    }
}