using System.Threading.Tasks;
using ExternalDependencies.AuditoriumLayoutRepository;
using ExternalDependencies.ReservationsProvider;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Infra.Adapter;

namespace SeatsSuggestions.Tests.UnitTests
{
    public class AuditoriumSeatingRepositoryShould
    {
        [Test]
        public async Task Retrieve_and_save_auditorium_seating_instances_from_repository()
        {
            var auditoriumSeatingAdapter = new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

            var auditoriumSeatingRepository = new AuditoriumSeatingRepository(auditoriumSeatingAdapter);

            var auditoriumSeating1 = await auditoriumSeatingRepository.GetById(new ShowId("1"));
            
            auditoriumSeatingRepository.Save(auditoriumSeating1);
                
            var auditoriumSeating2 = await auditoriumSeatingRepository.GetById(new ShowId("1"));

            Check.That(auditoriumSeating1).IsEqualTo(auditoriumSeating2);

            // Build a refresh instance of auditoriumSeating without repository
            var auditoriumSeating3 =  await auditoriumSeatingAdapter.GetAuditoriumSeating(new ShowId("2"));
            // then save it in the repository
            auditoriumSeatingRepository.Save(auditoriumSeating3);
            // and retrieve it in the repository
            var auditoriumSeating4 = await auditoriumSeatingRepository.GetById(new ShowId("2"));

            Check.That(auditoriumSeating3).IsEqualTo(auditoriumSeating4);
        }
    }
}
