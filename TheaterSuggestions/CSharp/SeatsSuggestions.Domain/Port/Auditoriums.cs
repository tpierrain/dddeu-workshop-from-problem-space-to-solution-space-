using System.Threading.Tasks;

namespace SeatsSuggestions.Domain.Port
{
    public interface Auditoriums    
    {
        Task<AuditoriumSeating> FindById(ShowId showId);
        void Save(AuditoriumSeating auditoriumSeating);
    }
}