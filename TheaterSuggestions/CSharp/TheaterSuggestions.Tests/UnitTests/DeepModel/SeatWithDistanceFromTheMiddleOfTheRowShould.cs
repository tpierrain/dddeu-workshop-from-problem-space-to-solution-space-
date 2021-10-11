using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Domain.DeepModel;

namespace SeatsSuggestions.Tests.UnitTests.DeepModel
{
    class SeatWithDistanceFromTheMiddleOfTheRowShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var firstInstance = new SeatWithTheDistanceFromTheMiddleOfTheRow(
                new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available), 
                5);            
            var secondInstance = new SeatWithTheDistanceFromTheMiddleOfTheRow(
                new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available), 
                5);

            Check.That(firstInstance).IsEqualTo(secondInstance);
            Check.That(firstInstance.ToString()).IsEqualTo(secondInstance.ToString());
        }
    }
}
