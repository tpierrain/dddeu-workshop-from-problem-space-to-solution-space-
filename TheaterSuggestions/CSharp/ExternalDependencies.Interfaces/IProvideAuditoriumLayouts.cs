namespace ExternalDependencies.Interfaces
{
    public interface IProvideAuditoriumLayouts
    {
        AuditoriumDto GetAuditoriumSeatingFor(string showId);
    }
}