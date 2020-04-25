using System.Threading.Tasks;
using ExternalDependencies;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;
using SeatsSuggestions.Infra.Helpers;

namespace SeatsSuggestions.Infra
{
    /// <summary>
    ///     Get via a web api and adapt reservations and return <see cref="ReservedSeatsDto" />.
    /// </summary>
    public class SeatReservationsWebRepository : IProvideCurrentReservations
    {
        private readonly string _uriSeatReservationService;
        private readonly WebClient _webClient;

        public SeatReservationsWebRepository(string uriSeatReservationService, WebClient webClient)
        {
            _uriSeatReservationService = uriSeatReservationService;
            _webClient = webClient;
        }

        public async Task<ReservedSeatsDto> GetReservedSeats(string showId)
        {
            var jsonSeatReservations = await GetDataForReservations(showId);

            var reservationsSeatsDto = JsonConvert.DeserializeObject<ReservedSeatsDto>(jsonSeatReservations,
                    new JsonSerializerSettings {ContractResolver = new CamelCasePropertyNamesContractResolver()});

            return reservationsSeatsDto;
        }

        private async Task<string> GetDataForReservations(string showId)
        {
            var response = await _webClient.GetAsync($"{_uriSeatReservationService}api/data_for_reservation_seats/{showId}");

            response.EnsureSuccessStatusCode();

            return await response.Content.ReadAsStringAsync();
        }
    }
}