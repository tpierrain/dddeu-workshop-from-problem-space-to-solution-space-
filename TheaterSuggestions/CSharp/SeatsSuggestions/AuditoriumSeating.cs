using System.Collections.Generic;
using Value;
using Value.Shared;

namespace SeatsSuggestions
{
    public class AuditoriumSeating : ValueType<AuditoriumSeating>
    {
        public IReadOnlyDictionary<string, Row> Rows => _rows;

        private readonly Dictionary<string, Row> _rows;

        public AuditoriumSeating(Dictionary<string, Row> rows)
        {
            _rows = rows;
        }

        public SeatAllocation MakeAllocationFor(int partyRequested, PricingCategory pricingCategory)
        {
            foreach (var row in _rows.Values)
            {
                var seatAllocation = row.FindAllocation(partyRequested, pricingCategory);

                if (seatAllocation.MatchExpectation())
                {
                    return seatAllocation;
                }
            }

            return new AllocationNotAvailable(partyRequested, pricingCategory);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] {new DictionaryByValue<string, Row>(_rows)};
        }

        public AuditoriumSeating MarkAsAlreadySuggested(IEnumerable<Seat> updatedSeats)
        {
            var newVersionOfRows = new Dictionary<string, Row>(_rows);

            foreach (var updatedSeat in updatedSeats)
            {
                var formerRow = newVersionOfRows[updatedSeat.RowName];
                var newVersionOfRow = formerRow.UpdateSeat(updatedSeat);
                newVersionOfRows[updatedSeat.RowName] = newVersionOfRow;
            }

            return new AuditoriumSeating(newVersionOfRows);
        }

        public AuditoriumSeating MarkSeatsAsSuggested(SeatAllocation seatAllocation)
        {
            // Prepare the new list of Seats for this
            var updatedSeats = new List<Seat>();
            foreach (var seat in seatAllocation.Seats)
            {
                var updatedSeat = seat.MarkAsAlreadySuggested();
                updatedSeats.Add(updatedSeat);
            }

            // Update the seat references in the Auditorium
            var newAuditorium = this.MarkAsAlreadySuggested(updatedSeats);

            return newAuditorium;
        }
    }
}