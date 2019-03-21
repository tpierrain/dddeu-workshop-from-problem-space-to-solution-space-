using System.Threading.Tasks;

namespace SeatsSuggestions.Domain.Port
{
    public interface IProvideAuditoriumSeating
    {
        Task<SuggestionsMade> MakeSuggestions(ShowId showId, PartyRequested partyRequested);
    }
}