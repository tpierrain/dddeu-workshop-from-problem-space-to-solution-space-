using ExternalDependencies;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.DependencyInjection;
using SeatsSuggestions.Domain;
using SeatsSuggestions.Domain.Port;
using SeatsSuggestions.Infra.Adapter;

namespace SeatsSuggestions.Api
{
    public class Startup
    {
        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_1);

            // api/data_for_auditoriumSeating/
            IProvideAuditoriumLayouts auditoriumSeatingRepository = new AuditoriumWebRepository("http://localhost:6000/");

            // data_for_reservation_seats/
            IProvideCurrentReservations seatReservationsProvider = new SeatReservationsWebRepository("http://localhost:5000/");
            var seatAllocator =
                new SeatAllocator(new AuditoriumSeatingAdapter(auditoriumSeatingRepository, seatReservationsProvider));
            services.AddSingleton<IProvideAuditoriumSeating>(seatAllocator);
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseMvc();
        }
    }
}
