using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace SeatsSuggestions
{
    public class AdjacentSeats : IEnumerable<Seat>
    {
        private readonly List<Seat> _seats;

        public AdjacentSeats(IEnumerable<Seat> seats)
        {
            _seats = seats.ToList();
        }

        public IEnumerator<Seat> GetEnumerator()
        {
            return _seats.GetEnumerator();
        }

        public IEnumerable<AdjacentSeats> SplitInto(int size)
        {
            var result = new List<AdjacentSeats>();
            var lastGroup = new AdjacentSeats(new List<Seat>());
            foreach (var seat in _seats)
            {
                if (lastGroup.Count() == size)
                {
                    result.Add(lastGroup);
                    lastGroup = new AdjacentSeats(new List<Seat>()) {seat};
                }
                else
                {
                    lastGroup.Add(seat);
                }
            }

            if (lastGroup.Count() == size)
            {
                result.Add(lastGroup);
            }

            return result;
        }

        private void Add(Seat seat)
        {
            _seats.Add(seat);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public int ComputeDistanceFromRowCentroid(int rowSize)
        {
            var allSeatsDistanceFromRowCenter = _seats.Select(s => s.ComputeDistanceFromRowCentroid(rowSize)).ToList();
            var computeDistanceFromRowCentroid = allSeatsDistanceFromRowCenter.Sum() / _seats.Count;
            return computeDistanceFromRowCentroid;
        }


        public override string ToString()
        {
            return $"{string.Join("-", _seats.Select(s => s.ToString()))}";
        }
    }
}