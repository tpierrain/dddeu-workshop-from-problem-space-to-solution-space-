using System.Threading.Tasks;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Api.Controllers;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Tests.Tools;

namespace SeatsSuggestions.Tests.AcceptanceTests
{
    [TestFixture]
    public class SeatsSuggestionsControllerShould
    {
        [Test]
        public async Task Return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved()
        {
            var showId = "5";
            var partyRequested = 1;

            var webClient = Stub.AWebClientWith(showId, "5-Madison Theater-(2)(0)_theater.json", "5-Madison Theater-(2)(0)_booked_seats.json");
            var hexagon = Configure.Hexagon(webClient);
            var leftSideAdapter = new SeatsSuggestionsController(hexagon);

            var response = await leftSideAdapter.Get(showId, partyRequested);
            var suggestionsMade = response.ExtractValue<SuggestionsMade>();

            Check.That(suggestionsMade.PartyRequested.PartySize).IsEqualTo(partyRequested);
            Check.That(suggestionsMade.ShowId.Id).IsEqualTo(showId);

            Check.That(suggestionsMade).IsInstanceOf<SuggestionNotAvailable>();
        }

        [Test]
        public async Task Suggest_one_seat_when_Auditorium_contains_one_available_seat_only()
        {
            var showId = "1";
            var partyRequested = 1;

            var webClient = Stub.AWebClientWith(showId, "1-Ford Theater-(2)(0)_theater.json", "1-Ford Theater-(2)(0)_booked_seats.json");
            var hexagon = Configure.Hexagon(webClient);
            var leftSideAdapter = new SeatsSuggestionsController(hexagon);

            var response = await leftSideAdapter.Get(showId, partyRequested);
            var suggestionsMade = response.ExtractValue<SuggestionsMade>();

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A3");
        }

        [Test]
        public async Task Offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity()
        {
            var showId = "18";
            var partyRequested = 1;

            var webClient = Stub.AWebClientWith(showId, "18-New Amsterdam-(6)(0)_theater.json", "18-New Amsterdam-(6)(0)_booked_seats.json");
            var hexagon = Configure.Hexagon(webClient);
            var leftSideAdapter = new SeatsSuggestionsController(hexagon);

            var response = await leftSideAdapter.Get(showId, partyRequested);
            var suggestionsMade = response.ExtractValue<SuggestionsMade>();

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A5", "A6", "A4");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Second)).ContainsExactly("A2", "A9", "A1");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Third)).ContainsExactly("E5", "E6", "E4");

            Check.That(suggestionsMade.SeatNames(PricingCategory.Mixed)).ContainsExactly("A5", "A6", "A4");
        }

        [Test]
        public async Task Offer_adjacent_seats_nearer_the_middle_of_a_row()
        {
            var showId = "9";
            var partyRequested = 1;

            var webClient = Stub.AWebClientWith(showId, "9-Mogador Theater-(2)(0)_theater.json", "9-Mogador Theater-(2)(0)_booked_seats.json");
            var hexagon = Configure.Hexagon(webClient);
            var leftSideAdapter = new SeatsSuggestionsController(hexagon);

            var response = await leftSideAdapter.Get(showId, partyRequested);
            var suggestionsMade = response.ExtractValue<SuggestionsMade>();

            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).ContainsExactly("A4", "A3", "B5");
        }

        [Test]
        public async Task Offer_adjacent_seats_nearer_the_middle_of_a_row_when_it_is_possible()
        {
            var showId = "3";
            var partyRequested = 4;

            var webClient = Stub.AWebClientWith(showId, "3-Dock Street Theater-(6)(0)_theater.json", "3-Dock Street Theater-(6)(0)_booked_seats.json");
            var hexagon = Configure.Hexagon(webClient);
            var leftSideAdapter = new SeatsSuggestionsController(hexagon);

            var response = await leftSideAdapter.Get(showId, partyRequested);
            var suggestionsMade = response.ExtractValue<SuggestionsMade>();


            Check.That(suggestionsMade.SeatNames(PricingCategory.First)).IsEmpty();
            Check.That(suggestionsMade.SeatNames(PricingCategory.Second)).ContainsExactly("C4-C5-C6-C7", "D4-D5-D6-D7");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Third)).ContainsExactly("E4-E5-E6-E7", "F4-F5-F6-F7");
            Check.That(suggestionsMade.SeatNames(PricingCategory.Mixed)).ContainsExactly("A6-A7-A8-A9", "C4-C5-C6-C7", "D4-D5-D6-D7");
        }
    }
}