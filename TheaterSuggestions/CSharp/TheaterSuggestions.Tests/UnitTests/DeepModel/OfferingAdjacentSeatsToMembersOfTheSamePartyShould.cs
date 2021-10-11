using System.Collections.Generic;
using System.Linq;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Domain.DeepModel;
using SeatsSuggestions.Domain;

namespace SeatsSuggestions.Tests.UnitTests.DeepModel
{
    internal class OfferingAdjacentSeatsToMembersOfTheSamePartyShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var firstInstance =
                new OfferingAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(new PartyRequested(3), PricingCategory.Mixed));
            var secondInstance =
                new OfferingAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(new PartyRequested(3), PricingCategory.Mixed));

            Check.That(firstInstance).IsEqualTo(secondInstance);
        }

        [Test]
        public void Offer_groups_of_adjacent_seats_when_row_contains_some_reserved_seats()
        {
            var partyRequested = new PartyRequested(3);
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 });
            var seatsWithDistance =
                new OfferingSeatsNearerMiddleOfTheRow(row).OfferSeatsNearerTheMiddleOfTheRow(
                    new SuggestionRequest(partyRequested, PricingCategory.Mixed)).Take(partyRequested.PartySize);

            Check.That(new OfferingAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(partyRequested,
                    PricingCategory.Mixed))
                .OfferAdjacentSeats(seatsWithDistance)).ContainsExactly(a5, a6, a7);
        }


        [Test]
        public void Offer_adjacent_seats_nearer_the_middle_of_row()
        {
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 });

            Check.That(row.OfferAdjacentSeatsNearerTheMiddleOfRow(new SuggestionRequest(new PartyRequested(3), PricingCategory.Mixed)))
                .ContainsExactly(a5, a6, a7);
        }

        [Test]
        public void Offer_adjacent_seats_nearer_the_middle_of_row_when_the_middle_is_reserve()
        {
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Reserved);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Reserved);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Reserved);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Reserved);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 });

            Check.That(row.OfferAdjacentSeatsNearerTheMiddleOfRow(new SuggestionRequest(new PartyRequested(2)
                    , PricingCategory.Mixed)))
                .ContainsExactly(a2, a1);
        }
    }
}