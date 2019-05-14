using System.Threading.Tasks;

namespace SeatsSuggestions
{
    public interface IAdaptAuditoriumSeating
    {
        Task<AuditoriumSeating> GetAuditoriumSeating(string showId);
    }
}