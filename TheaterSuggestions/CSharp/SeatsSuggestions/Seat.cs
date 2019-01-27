using System;
using System.Collections.Generic;
using Value;

namespace SeatsSuggestions
{
    public class Seat : ValueType<Seat>
    {
        public Seat(string rowName, uint number, PricingCategory pricingCategory, SeatAvailability seatAvailability)
        {
            RowName = rowName;
            Number = number;
            PricingCategory = pricingCategory;
            SeatAvailability = seatAvailability;
        }

        public string RowName { get; }
        public uint Number { get; }
        public PricingCategory PricingCategory { get; }
        public SeatAvailability SeatAvailability { get; }

        public bool IsAvailable()
        {
            return SeatAvailability == SeatAvailability.Available;
        }

        public override string ToString()
        {
            return $"{RowName}{Number}";
        }

        public bool MatchCategory(PricingCategory pricingCategory)
        {
            if (pricingCategory == PricingCategory.Mixed)
            {
                return true;
            }

            return PricingCategory == pricingCategory;
        }

        public Seat Allocate()
        {
            if (SeatAvailability == SeatAvailability.Available)
            {
                return new Seat(RowName, Number, PricingCategory, SeatAvailability.Allocated);
            }

            return this;
        }

        public bool SameSeatLocation(Seat seat)
        {
            return RowName == seat.RowName && Number == seat.Number;
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] {RowName, Number, PricingCategory, SeatAvailability};
        }

        public bool IsAdjacentWith(uint number)
        {
            return Number + 1 == number || Number - 1 == number;
        }

        public int ComputeDistanceFromRowCentroid(int rowSize)
        {
            var distance = 0;
            var centroidIndex = Math.Abs(rowSize / 2);

            if (rowSize % 2 == 0)
            {
                if (Number == centroidIndex || Number == centroidIndex + 1) return 0;
                if (Number < centroidIndex)
                {
                    distance = (int) Math.Abs(Number - centroidIndex);
                }
                else
                {
                    distance = (int) Math.Abs(Number - centroidIndex) - 1;
                }
            }
            else
            {
                distance = (int) Math.Abs(Number - centroidIndex);
            }

            return distance;
        }
    }
}