namespace SeatsSuggestions
{
    internal class SeatingOptionNotAvailable : SeatOptionsSuggested
    {
        public SeatingOptionNotAvailable(int partyRequested, PricingCategory pricingCategory) : base(partyRequested,
            pricingCategory)
        {
        }
    }
}