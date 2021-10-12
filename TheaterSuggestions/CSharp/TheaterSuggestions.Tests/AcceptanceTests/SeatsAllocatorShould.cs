using ExternalDependencies.AuditoriumLayoutRepository;
using ExternalDependencies.ReservationsProvider;
using NFluent;
using NUnit.Framework;

namespace SeatsSuggestions.Tests.AcceptanceTests
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
            const string showId = "18";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionsMade = seatAllocator.MakeSuggestions(showId, partyRequested);

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A5", "A6", "A4");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Second)).ContainsExactly( "A2", "A9", "A1");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Third)).ContainsExactly("E5", "E6", "E4");

            Check.That(suggestionsMade.SeatNames(PricingCategory.Mixed)).ContainsExactly("A5", "A6", "A4");
        }

        [Test]
        public void Offer_seats_nearer_the_middle_of_a_row()
        {
            // FIX ME
            const string showId = "9";
            const int partyRequested = 1;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionsMade = seatAllocator.MakeSuggestions(showId, partyRequested);

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A4", "A3", "B5");
        }

        [Test]
        public void Offer_adjacent_seats_nearer_the_middle_of_a_row_when_it_is_possible()
        {
            // FIX ME
            const string showId = "3";
            const int partyRequested = 4;

            var auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

            var suggestionsMade = seatAllocator.MakeSuggestions(showId, partyRequested);

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).IsEmpty();
            Check.That(suggestionsMade.SeatNames(PricingCategory.Second))
                .ContainsExactly("C4-C5-C6-C7", "D4-D5-D6-D7");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Third))
                .ContainsExactly("E4-E5-E6-E7", "F4-F5-F6-F7");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Mixed))
                .ContainsExactly("A6-A7-A8-A9", "C4-C5-C6-C7", "D4-D5-D6-D7");
        }
    }
}