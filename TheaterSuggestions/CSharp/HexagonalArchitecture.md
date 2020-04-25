# Hexagonal Architecture sample

This solution is used to support various hands-on labs built and used for one of our DDD training course.


The "__Hexagonal__" branch of this repo hosts a typical Hexagonal Architecture structure. It also hosts an example of the testing strategy illustrated in Thomas PIERRAIN's blog post: "http://tpierrain.blogspot.com/2020/03/hexagonal-architecture-dont-get-lost-on.html".

## About the testing strategy

This testing strategy includes all the Adapters in the coarse-grained Acceptance tests, stubbing only the last-miles I/Os (here Http calls to external web APIs) so that we are catching all our mistakes in those right-side Adapters (here only one: the AuditoriumSeatingAdapter and all its collaborators:  SeatReservationsWebClient and AuditoriumWebRepository).






