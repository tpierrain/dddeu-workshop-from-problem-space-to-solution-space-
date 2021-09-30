using System.Collections.Generic;
using System.Threading.Tasks;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Domain.Port;
namespace SeatsSuggestions.Infra.Adapter
{
    public class AuditoriumSeatingRepository : IRetrieveAuditoriumSeating
    {
        private readonly AuditoriumSeatingAdapter _auditoriumLayoutAdapter;
        private readonly Dictionary<ShowId, AuditoriumSeating> _repository = new Dictionary<ShowId, AuditoriumSeating>();

        public AuditoriumSeatingRepository(AuditoriumSeatingAdapter auditoriumLayoutAdapter)
        {
            _auditoriumLayoutAdapter = auditoriumLayoutAdapter;
        }

        public async Task<AuditoriumSeating> GetById(ShowId showId)
        {
            var auditoriumSeating = await _auditoriumLayoutAdapter.GetAuditoriumSeating(showId);
            if (!_repository.ContainsKey(showId))
                _repository.Add(showId, auditoriumSeating);

            return auditoriumSeating;
        }

        public void Save(AuditoriumSeating auditoriumSeating)
        {
            _repository[auditoriumSeating.ShowId] = auditoriumSeating;
        }
    }
}