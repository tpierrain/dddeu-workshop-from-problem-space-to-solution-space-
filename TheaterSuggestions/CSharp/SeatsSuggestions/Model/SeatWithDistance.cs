using System.Collections.Generic;
using Value;

namespace SeatsSuggestions.Model
{
    public class SeatWithDistance : ValueType<Seat>
    {
        public SeatWithDistance(Seat seat, int distanceFromTheMiddle)
        {
            Seat = seat;
            DistanceFromTheMiddle = distanceFromTheMiddle;
        }

        public Seat Seat { get; }
        public int DistanceFromTheMiddle { get; }

        public override string ToString()
        {
            return $"{Seat.RowName}{Seat.Number}";
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { Seat, DistanceFromTheMiddle };
        }
    }
}