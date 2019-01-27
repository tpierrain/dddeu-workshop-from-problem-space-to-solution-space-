using System.Collections.Generic;

namespace ExternalDependencies.Interfaces
{
    public class CorridorDto
    {
        public int Number { get; set; }
        public IEnumerable<string> InvolvedRowNames { get; set; }
    }
}