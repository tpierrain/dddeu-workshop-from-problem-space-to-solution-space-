using System.Collections.Generic;
using System.Linq;
using NFluent;
using NUnit.Framework;

namespace SeatsSuggestions.Tests.UnitTests
{
    [TestFixture]
    public class SeatCollectionExtensionsShould
    {
        [Test]
        public void SelectAvailableSeatsCompliantWith_when_all_are_First_and_requesting_First()
        {
            var seats = new List<Seat>
            {
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
            };

            var seatsCompliant = seats.SelectAvailableSeatsCompliant(PricingCategory.First);
            Check.That(seatsCompliant.Select(s => s.ToString()))
                .ContainsExactly("A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10");
        }


        [Test]
        public void SelectAvailableSeatsCompliantWith_when_some_are_second_and_requesting_second()
        {
            var seats = new List<Seat>
            {
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
            };

            var seatsCompliant = seats.SelectAvailableSeatsCompliant(PricingCategory.Second);
            Check.That(seatsCompliant.Select(s => s.ToString())).ContainsExactly("A3", "A6");
        }

        [Test]
        public void Return_empty_when_SelectAdjacentSeats_is_called_and_no_adjacentSeats_are_available()
        {
            var seats = new List<Seat>
            {
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available)
            };

            var adjacentSeats = seats.SelectAdjacentSeats(2);
            Check.That(adjacentSeats).IsEmpty();
        }

        [Test]
        public void Return_a_few_adjacent_seats_when_SelectAdjacentSeats_is_called()
        {
            var seats = new List<Seat>
            {
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),

                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.Second, SeatAvailability.Available),

                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
            };

            var adjacentSeats = seats.SelectAdjacentSeats(2);
            Check.That(adjacentSeats.Select(s => s.ToString())).ContainsExactly("A3-A4", "A5-A6", "A8-A9");
        }

        [Test]
        public void Order_by_middle_of_the_Row()
        {
            var seats = new List<Seat>
            {
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),

                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.Second, SeatAvailability.Available),

                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
            };

            var adjacentSeats = seats.SelectAdjacentSeats(2);
            var orderByMiddleOfTheRow = adjacentSeats.OrderByMiddleOfTheRow(10);
            Check.That(orderByMiddleOfTheRow.Select(a => a.ToString())).ContainsExactly("A5-A6", "A3-A4", "A8-A9");
        }

        [Test]
        public void Order_by_middle_of_the_Row_when_every_seat_is_available()
        {
            var seats = new List<Seat>
            {
                new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available)
            };

            var adjacentSeats = seats.SelectAdjacentSeats(1);
            var orderByMiddleOfTheRow = adjacentSeats.OrderByMiddleOfTheRow(10);
            Check.That(orderByMiddleOfTheRow.Select(a => a.ToString()))
                .ContainsExactly("A5", "A6", "A4", "A7", "A3", "A8", "A2", "A9", "A1", "A10");
        }
    }
}