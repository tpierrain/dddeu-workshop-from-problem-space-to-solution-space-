using SeatsSuggestions.Domain;
using SeatsSuggestions.Domain.Ports;
using SeatsSuggestions.Infra;
using SeatsSuggestions.Infra.Adapter;
using SeatsSuggestions.Infra.Helpers;

namespace SeatsSuggestions.Tests.Tools
{
    public static class Configure
    {
        public static IRequestSuggestions Hexagon(IWebClient webClient)
        {
            // Instantiate the Right-side adapter(s)
            var rightSideAdapter = new AuditoriumSeatingAdapter(new AuditoriumWebRepository("http://fakehost:50950/", webClient), new SeatReservationsWebRepository("http://fakehost:50951/", webClient));

            // Give them to the hexagon
            var hexagon = new SeatAllocator(rightSideAdapter);
            return hexagon;
        }
    }
}