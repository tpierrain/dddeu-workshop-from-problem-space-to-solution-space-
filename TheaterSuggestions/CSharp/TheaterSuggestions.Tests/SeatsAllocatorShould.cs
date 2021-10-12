﻿using NUnit.Framework;

namespace SeatsSuggestions.Tests
{
    [TestFixture]
    public class SeatsAllocatorShould
    {
        [Test]
        public void Suggest_one_seat_when_Auditorium_contains_one_available_seat_only()
        {
            // Ford Auditorium-1
            //
            //       1   2   3   4   5   6   7   8   9  10
            //  A : (2) (2)  1  (1) (1) (1) (1) (1) (2) (2)
            //  B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
           
        }

        [Test]
        public void Return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved()
        {
            // Madison Auditorium-5
            //
            //     1   2   3   4   5   6   7   8   9  10
            //  A: 2   2   1   1   1   1   1   1   2   2
            //  B: 2   2   1   1   1   1   1   1   2   2
            //  C: 2   2   2   2   2   2   2   2   2   2
            //  D: 2   2   2   2   2   2   2   2   2   2
            //  E: 3   3   3   3   3  (3)  3   3   3   3
            //  F: 3   3   3   3   3   3   3   3   3   3
           
        }
    }
}