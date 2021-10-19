using System.Collections.Generic;

namespace SeatsSuggestions.Domain.DeepModel
{
    public class AdjacentSeats
    {
        public List<SeatWithTheDistanceFromTheMiddleOfTheRow> SeatsWithDistance { get; } = new();

        public AdjacentSeats(IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithTheDistance)
        {
            SeatsWithDistance.AddRange(seatsWithTheDistance);
        }

        public void AddSeat(SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistance)
        {
            SeatsWithDistance.Add(seatWithTheDistance);
        }

        public override string ToString()
        {
            return string.Join("-", SeatsWithDistance);
        }
    }
}
