using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class TheaterLayout
    {
        public readonly Dictionary<string, Row> Rows;

        public TheaterLayout(Dictionary<string, Row> Rows)
        {
            this.Rows = Rows;
        }
    }
}