using System.Threading.Tasks;

namespace SeatsSuggestions.Domain.Ports
{
    public interface IRequestSuggestions
    {
        Task<SuggestionsMade> MakeSuggestions(ShowId showId, PartyRequested partyRequested);
    }
}