namespace ExternalDependencies.Interfaces
{
    public interface IProvideCurrentReservations
    {
        ReservedSeatsDto GetReservedSeats(string showId);
    }
}