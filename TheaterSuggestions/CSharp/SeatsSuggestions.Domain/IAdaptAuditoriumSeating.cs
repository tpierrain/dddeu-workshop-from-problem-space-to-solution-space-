using System.Threading.Tasks;

namespace SeatsSuggestions.Domain
{
    public interface IAdaptAuditoriumSeating
    {
        Task<AuditoriumSeating> GetAuditoriumSeating(string showId);
    }
}