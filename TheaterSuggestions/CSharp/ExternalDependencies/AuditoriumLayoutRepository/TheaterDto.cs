using System.Collections.Generic;

namespace ExternalDependencies.Theater
{
    public class TheaterDto
    {
        public Dictionary<string, IReadOnlyList<SeatDto>> Rows { get; set; }
        public IEnumerable<CorridorDto> Corridors { get; set; }
    }
}