using System.Collections.Generic;
using Value;

namespace SeatsSuggestions.DeepModel
{
    /// <summary>
    /// 
    /// Our model uses a seat with a property DistanceFromTheMiddle
    /// To manage business rules:
    /// 
    ///     * Offer seats nearer middle of the row.
    ///     * Offer adjacent seats to member of the same party
    /// 
    /// </summary>
    public class SeatWithDistanceFromTheMiddleOfTheRow : ValueType<Seat>
    {
        public SeatWithDistanceFromTheMiddleOfTheRow(Seat seat, int distanceFromTheMiddle)
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