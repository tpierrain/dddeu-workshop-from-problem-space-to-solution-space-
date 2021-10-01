using System.Collections.Generic;
using System.Threading.Tasks;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Domain.Port;
namespace SeatsSuggestions.Infra.Adapter
{
    public class AuditoriumsRepository : Auditoriums
    {
        private readonly AuditoriumSeatingAdapter _auditoriumLayoutAdapter;
        private readonly Dictionary<ShowId, AuditoriumSeating> _auditoriums = new Dictionary<ShowId, AuditoriumSeating>();

        public AuditoriumsRepository(AuditoriumSeatingAdapter auditoriumLayoutAdapter)
        {
            _auditoriumLayoutAdapter = auditoriumLayoutAdapter;
        }

        public async Task<AuditoriumSeating> FindById(ShowId showId)
        {
            if (!_auditoriums.ContainsKey(showId))
            {
                var  auditoriumSeating = await _auditoriumLayoutAdapter.GetAuditoriumSeating(showId);
                _auditoriums.Add(showId, auditoriumSeating);
            }

            return _auditoriums[showId];
        }

        public void Save(AuditoriumSeating auditoriumSeating)
        {
            var showId = auditoriumSeating.ShowId;

            if (!_auditoriums.ContainsKey(showId))
            {
                _auditoriums.Add(showId, auditoriumSeating);
            }
            else
            {
                _auditoriums[showId] = auditoriumSeating;
            }
        }
    }
}