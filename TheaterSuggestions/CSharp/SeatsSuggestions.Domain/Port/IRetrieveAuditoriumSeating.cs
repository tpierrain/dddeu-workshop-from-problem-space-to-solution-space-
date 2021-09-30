using System.Threading.Tasks;

namespace SeatsSuggestions.Domain.Port
{
    public interface IRetrieveAuditoriumSeating    
    {
        Task<AuditoriumSeating> GetById(ShowId showId);
        void Save(AuditoriumSeating auditoriumSeating);
    }
}