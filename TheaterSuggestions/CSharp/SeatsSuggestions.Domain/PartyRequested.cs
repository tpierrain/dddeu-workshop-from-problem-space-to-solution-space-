using System;
using System.Collections.Generic;
using Value;

namespace SeatsSuggestions.Domain
{
    public class PartyRequested : ValueType<PartyRequested>
    {
        public PartyRequested(int partySize)
        {
            if (partySize <= 0 || partySize > 20)
                throw new ArgumentException($"{nameof(partySize)} should be greater than zero and less than 20");

            PartySize = partySize;
        }

        public int PartySize { get; }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { PartySize };
        }
    }
}