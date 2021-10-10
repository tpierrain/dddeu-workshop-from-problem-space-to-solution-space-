using System;
using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.DeepModel
{
    /// <summary>
    ///     Business Rule: offer seats nearer the middle of a row
    /// </summary>
    public class OfferSeatsNearerMiddleOfTheRow : ValueType<OfferSeatsNearerMiddleOfTheRow>
    {
        private readonly Row _row;

        public OfferSeatsNearerMiddleOfTheRow(Row row)
        {
            _row = row;
        }

        public IEnumerable<SeatWithDistanceFromTheMiddleOfTheRow> SuggestSeatsNearerTheMiddleOfTheRow(
            SuggestionRequest suggestionRequest)
        {
            return ComputeDistancesNearerTheMiddleOfTheRow().Where(seatWithDistance =>
                    seatWithDistance.Seat.MatchCategory(suggestionRequest.PricingCategory))
                .Where(seatWithDistance => seatWithDistance.Seat.IsAvailable());
        }

        private IEnumerable<SeatWithDistanceFromTheMiddleOfTheRow> ComputeDistancesNearerTheMiddleOfTheRow()
        {
            var seatsWithDistancesFromTheMiddle = SplitSeatsByDistanceNearerTheMiddleOfTheRow(IsMiddle);

            return seatsWithDistancesFromTheMiddle.Append(GetSeatsInTheMiddleOfTheRow())
                .SelectMany(seatWithDistanceFromTheMiddleOfTheRows => seatWithDistanceFromTheMiddleOfTheRows)
                .OrderBy(s => s.DistanceFromTheMiddleOfTheRow);
        }

        private IEnumerable<SeatWithDistanceFromTheMiddleOfTheRow> GetSeatsInTheMiddleOfTheRow()
        {
            return GetSeatsInTheMiddleOfTheRow(_row.Seats, GetTheMiddleOfRow())
                .Select(s => new SeatWithDistanceFromTheMiddleOfTheRow(s, 0));
        }

        private int GetTheMiddleOfRow()
        {
            return _row.Seats.Count % 2 == 0 ? _row.Seats.Count / 2 : Math.Abs(_row.Seats.Count / 2) + 1;
        }

        private static IEnumerable<Seat> GetSeatsInTheMiddleOfTheRow(IReadOnlyList<Seat> seats, int middle)
        {
            return seats.Count % 2 == 0
                ? new List<Seat> { seats[middle - 1], seats[middle] }
                : new List<Seat> { seats[middle - 1] };
        }

        private bool IsMiddle(Seat seat)
        {
            var middle = GetTheMiddleOfRow();

            switch (_row.Seats.Count % 2)
            {
                case 0 when Math.Abs(seat.Number - middle) == 0:
                case 0 when seat.Number - (middle + 1) == 0:
                    return true;
                default:
                    return Math.Abs(seat.Number - middle) == 0;
            }
        }

        private IEnumerable<List<SeatWithDistanceFromTheMiddleOfTheRow>> SplitSeatsByDistanceNearerTheMiddleOfTheRow(
            Func<Seat, bool> predicate)
        {
            var middle = GetTheMiddleOfRow();

            var seatWithDistances = new List<SeatWithDistanceFromTheMiddleOfTheRow>();
            foreach (var seat in _row.Seats)
                if (!predicate(seat))
                {
                    int distance;
                    if (_row.Seats.Count % 2 == 0)
                        distance = seat.Number - middle > 0
                            ? Math.Abs((int)(seat.Number - middle))
                            : Math.Abs((int)(seat.Number - (middle + 1)));
                    else
                        distance = Math.Abs((int)(seat.Number - middle));

                    seatWithDistances.Add(new SeatWithDistanceFromTheMiddleOfTheRow(seat, distance));
                }
                else
                {
                    if (seatWithDistances.Count > 0)
                        yield return seatWithDistances;
                    seatWithDistances = new List<SeatWithDistanceFromTheMiddleOfTheRow>();
                }

            if (seatWithDistances.Count > 0)
                yield return seatWithDistances;
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { _row };
        }
    }
}