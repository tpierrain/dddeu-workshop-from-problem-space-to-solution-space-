using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Domain.Ports;
using SeatsSuggestions.Infra.Adapter;

namespace SeatsSuggestions.Api.Controllers
{
    /// <summary>
    /// Web controller acting as a left-side Adapter of a Hexagonal Architecture.
    /// </summary>
    [ApiController]
    [ApiVersion("1.0")]
    [Route("api/v{version:apiVersion}/[controller]")]
    public class SeatsSuggestionsController : ControllerBase
    {
        private readonly IProvideUpToDateAuditoriumSeating _auditoriumSeatingProvider;

        public SeatsSuggestionsController(IProvideUpToDateAuditoriumSeating auditoriumSeatingProvider)
        {
            _auditoriumSeatingProvider = auditoriumSeatingProvider;
        }

        // GET api/SeatsSuggestions?showId=5&party=3
        [HttpGet]
        public async Task<IActionResult> Get([FromQuery(Name = "showId")] string showId, [FromQuery(Name = "party")] int party)
        {           
            // Infra => Domain
            var id = new ShowId(showId);
            var partyRequested = new PartyRequested(party);
            var auditoriumSeating = await _auditoriumSeatingProvider.GetAuditoriumSeating(id);

            // Call the function core
            var suggestions = SeatAllocator.TryMakeSuggestions(id, partyRequested, auditoriumSeating)
                .GetValueOrFallback(new SuggestionNotAvailable(id, partyRequested));

            // Domain => Infra
            return new OkObjectResult(suggestions/*JsonConvert.SerializeObject(suggestions, Formatting.Indented)*/);
        }
    }
}