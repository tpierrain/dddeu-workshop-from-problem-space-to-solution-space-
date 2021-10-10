using System;
using System.Collections.Generic;
using System.Text;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.DeepModel;

namespace SeatsSuggestions.Tests.UnitTests
{
    class OfferSeatsNearerMiddleOfTheRowShould
    {
        [Test]
        public void Be_a_Value_Type()
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

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9 });
            
            var firstInstance = new OfferSeatsNearerMiddleOfTheRow(row);
            var secondInstance = new OfferSeatsNearerMiddleOfTheRow(row);

            Check.That(firstInstance).IsEqualTo(secondInstance);
        }
    }
}
