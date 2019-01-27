using System.Collections.Generic;
using System.Linq;

namespace SeatsSuggestions
{
    public static class SeatCollectionExtensions
    {
        public static IEnumerable<Seat> SelectAvailableSeatsCompliant(this IEnumerable<Seat> seats,
            PricingCategory pricingCategory)
        {
            return seats.Where(s => s.IsAvailable() && s.MatchCategory(pricingCategory));
        }

        public static IEnumerable<AdjacentSeats> SelectAdjacentSeats(this IEnumerable<Seat> seats, int size)
        {
            var adjacentSeatsGroups = new List<AdjacentSeats>();
            var adjacentSeats = new List<Seat>();

            if (size == 1) return seats.Select(s => new AdjacentSeats(new List<Seat> {s}));

            foreach (var candidateSeat in seats)
            {
                if (adjacentSeats.Count == 0)
                {
                    adjacentSeats.Add(candidateSeat);
                    continue;
                }

                if (candidateSeat.IsAdjacentWith(adjacentSeats.Last().Number))
                {
                    adjacentSeats.Add(candidateSeat);
                }
                else
                {
                    if (adjacentSeats.Count == 1)
                    {
                        adjacentSeats = new List<Seat> {candidateSeat};
                    }
                    else
                    {
                        adjacentSeatsGroups.Add(new AdjacentSeats(adjacentSeats));
                        adjacentSeats = new List<Seat> {candidateSeat};
                    }
                }
            }

            if (adjacentSeats.Count > 1)
            {
                adjacentSeatsGroups.Add(new AdjacentSeats(adjacentSeats));
            }

            if (adjacentSeatsGroups.Count == 0) adjacentSeatsGroups.Add(new AdjacentSeats(adjacentSeats));

            var groupsGreaterOrEqualThanSize = adjacentSeatsGroups.Where(a => a.Count() >= size);
            return groupsGreaterOrEqualThanSize.SelectMany(a => a.SplitInto(size));
        }

        public static IEnumerable<AdjacentSeats> OrderByMiddleOfTheRow(this IEnumerable<AdjacentSeats> adjacentSeats,
            int rowSize)
        {
            var sortedAdjacentSeatsGroups = new SortedList<int, List<AdjacentSeats>>();

            foreach (var adjacentSeat in adjacentSeats)
            {
                var distance = adjacentSeat.ComputeDistanceFromRowCentroid(rowSize);


                if (!sortedAdjacentSeatsGroups.ContainsKey(distance))
                {
                    sortedAdjacentSeatsGroups.Add(distance, new List<AdjacentSeats>());
                }

                sortedAdjacentSeatsGroups[distance].Add(adjacentSeat);
            }

            var orderByMiddleOfTheRow = sortedAdjacentSeatsGroups.Values.SelectMany(l => l);

            return orderByMiddleOfTheRow;
        }
    }
}