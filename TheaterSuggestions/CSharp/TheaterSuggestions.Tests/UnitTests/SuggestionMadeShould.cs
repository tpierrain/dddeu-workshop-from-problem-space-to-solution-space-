using System.Collections.Generic;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Domain;

namespace SeatsSuggestions.Tests.UnitTests
{
    public class SuggestionMadeShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var firstInstance = new SuggestionMade(new PartyRequested(3), PricingCategory.First,
                new List<Seat> { new Seat("A", 3, PricingCategory.First, SeatAvailability.Allocated) });
            var secondInstance = new SuggestionMade(new PartyRequested(3), PricingCategory.First,
                new List<Seat> { new Seat("A", 3, PricingCategory.First, SeatAvailability.Allocated) });

            Check.That(secondInstance.SuggestedSeats.Count).IsEqualTo(firstInstance.SuggestedSeats.Count);
            Check.That(firstInstance.SuggestedSeats.Count).IsEqualTo(secondInstance.SuggestedSeats.Count);

            // Two different instances with same values should be equals
            Check.That(secondInstance).IsEqualTo(firstInstance);
        }
    }
}
