package com.baasie.SeatsSuggestionsDomain;

import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuditoriumWebRepository implements IProvideAuditoriumLayouts {

    private String uriAuditoriumSeatingRepository;

    public AuditoriumWebRepository() {
        this.uriAuditoriumSeatingRepository = "http://localhost:8090/";
    }

    @Override
    public AuditoriumDto getAuditoriumSeatingFor(String showId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AuditoriumDto> response
                = restTemplate.getForEntity(uriAuditoriumSeatingRepository + "/api/data_for_auditoriumSeating/"+showId, AuditoriumDto.class);
        return response.getBody();
    }
}
