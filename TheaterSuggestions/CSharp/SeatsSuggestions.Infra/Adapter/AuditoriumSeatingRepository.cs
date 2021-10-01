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
            if (!_repository.ContainsKey(showId))
            {
                var  auditoriumSeating = await _auditoriumLayoutAdapter.GetAuditoriumSeating(showId);
                _repository.Add(showId, auditoriumSeating);
            }

            return _repository[showId];
        }

        public void Save(AuditoriumSeating auditoriumSeating)
        {
            var showId = auditoriumSeating.ShowId;

            if (!_repository.ContainsKey(showId))
            {
                _repository.Add(showId, auditoriumSeating);
            }
            else
            {
                _repository[showId] = auditoriumSeating;
            }
        }
    }
}