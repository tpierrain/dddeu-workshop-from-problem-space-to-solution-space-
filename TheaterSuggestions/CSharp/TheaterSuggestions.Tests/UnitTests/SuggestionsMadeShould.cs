using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Domain;

namespace SeatsSuggestions.Tests.UnitTests
{
    public class SuggestionsMadeShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var firstInstance = new SuggestionsMade(new ShowId("1"), new PartyRequested(1));
            var secondInstance = new SuggestionsMade(new ShowId("1"), new PartyRequested(1));

            Check.That(firstInstance.SeatsInFirstPricingCategory).IsEqualTo(secondInstance.SeatsInFirstPricingCategory);
            Check.That(firstInstance.SeatsInSecondPricingCategory).IsEqualTo(secondInstance.SeatsInSecondPricingCategory);
            Check.That(firstInstance.SeatsInThirdPricingCategory).IsEqualTo(secondInstance.SeatsInThirdPricingCategory);
            Check.That(firstInstance.SeatsInMixedPricingCategory).IsEqualTo(secondInstance.SeatsInMixedPricingCategory);

            Check.That(firstInstance).IsEqualTo(secondInstance);
        }
    }
}
