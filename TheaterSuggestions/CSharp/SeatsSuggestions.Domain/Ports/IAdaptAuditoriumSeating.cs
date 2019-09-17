using System.Threading.Tasks;

namespace SeatsSuggestions.Domain.Ports
{
    public interface IAdaptAuditoriumSeating
    {
        Task<AuditoriumSeating> GetAuditoriumSeating(ShowId showId);
    }
}