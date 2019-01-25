using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class AuditoriumLayout
    {
        public readonly Dictionary<string, Row> Rows;

        public AuditoriumLayout(Dictionary<string, Row> Rows)
        {
            this.Rows = Rows;
        }
    }
}