using NUnit.Framework;

namespace SeatsSuggestions.Tests
{
    [TestFixture]
    public class SeatAllocatorShould
    {
        /*
         * By respecting the Outside In approach of Test-Driven Development,
         * you should emerge the SeatAllocator service to produce your first seat suggestions
         * for a given party and a show Id.
         *
         * We offer you two concrete examples to allow you to realize
         * a happy path case and a failure case.
         */

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
            //      1   2   3   4   5   6   7   8   9  10
            // A : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
            // B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)

        }
    }
}