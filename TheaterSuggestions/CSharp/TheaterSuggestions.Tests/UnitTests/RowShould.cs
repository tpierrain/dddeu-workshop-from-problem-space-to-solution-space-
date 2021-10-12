﻿using System.Collections.Generic;
using System.Linq;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.DeepModel;

namespace SeatsSuggestions.Tests.UnitTests
{
    [TestFixture]
    public class RowShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);

            // Two different instances with same values should be equals
            var rowFirstInstance = new Row("A", new List<Seat> { a1, a2 });
            var rowSecondInstance = new Row("A", new List<Seat> { a1, a2 });
            Check.That(rowSecondInstance).IsEqualTo(rowFirstInstance);

            // Should not mutate existing instance 
            var a3 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            rowSecondInstance.AddSeat(a3);
            Check.That(rowSecondInstance).IsEqualTo(rowFirstInstance);
        }

        [Test]
        public void Offer_seas_from_the_middle_of_row_with_row_size_even_when_party_size_is_greater_than_one()
        {
            var partySize = 2;

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

            var seatsWithDistance = new OfferingSeatsNearerMiddleOfTheRow(row).OfferSeatsNearerTheMiddleOfTheRow(new SuggestionRequest(5, PricingCategory.Mixed)).Take(partySize);

            Check.That(seatsWithDistance.Select(s => s.Seat).ToList())
               .ContainsExactly(a5, a6);
        }


        [Test]
        public void Offer_seats_from_the_middle_of_row_with_row_size_odd_when_party_size_is_greater_than_one()
        {
            var partySize = 5;

            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9 });

            var seatsWithDistance = new OfferingSeatsNearerMiddleOfTheRow(row).OfferSeatsNearerTheMiddleOfTheRow(new SuggestionRequest(5, PricingCategory.Mixed)).Take(partySize);

            Check.That(seatsWithDistance.Select(s => s.Seat).OrderBy(s => s.Number).ToList())
                .ContainsExactly(a2, a3, a5, a6, a7);
        }

        [Test]
        public void Offer_group_of_adjacent_seats_when_row_contains_some_reserved_seats()
        {
            int partySize = 3;

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
                    new SuggestionRequest(5, PricingCategory.Mixed));

            var seats = OfferAdjacentSeats(seatsWithDistance, partySize);

            Check.That(seats).ContainsExactly(a5, a6, a7);
        }

        public IEnumerable<Seat> OfferAdjacentSeats(IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances, int partySize)
        {
            return new List<Seat>();
        }
    }
}