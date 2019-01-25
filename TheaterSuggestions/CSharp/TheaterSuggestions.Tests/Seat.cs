namespace SeatsSuggestions.Tests
{
    public class Seat
    {
        public Seat(string rowName, uint number, PricingCategory priceCategory, SeatAvailability seatAvailability)
        {
            RowName = rowName;
            Number = number;
            PriceCategory = priceCategory;
            SeatAvailability = seatAvailability;
        }

        public string RowName { get; }
        public uint Number { get; }
        public PricingCategory PriceCategory { get; }
        private SeatAvailability SeatAvailability { get; set; }

        public bool IsAvailable => SeatAvailability == SeatAvailability.Available;

        public override string ToString()
        {
            return $"{RowName}{Number}";
        }

        public void UpdateCategory(SeatAvailability seatAvailability)
        {
            SeatAvailability = seatAvailability;
        }
    }
}