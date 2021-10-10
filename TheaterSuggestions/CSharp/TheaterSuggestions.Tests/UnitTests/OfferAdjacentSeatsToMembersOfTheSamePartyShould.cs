using NFluent;
using NUnit.Framework;
using SeatsSuggestions.DeepModel;

namespace SeatsSuggestions.Tests.UnitTests
{
    class OfferAdjacentSeatsToMembersOfTheSamePartyShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var firstInstance = new OfferAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(3, PricingCategory.Mixed)); 
            var secondInstance = new OfferAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(3, PricingCategory.Mixed));

            Check.That(firstInstance).IsEqualTo(secondInstance);
        }
    }
}
