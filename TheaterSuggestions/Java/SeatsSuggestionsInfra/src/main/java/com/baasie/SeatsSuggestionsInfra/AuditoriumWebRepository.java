package com.baasie.SeatsSuggestionsInfra;

import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class AuditoriumWebRepository implements IProvideAuditoriumLayouts {

    private final String uriAuditoriumSeatingRepository;

    public AuditoriumWebRepository(String uriAuditoriumSeatingRepository) {
        this.uriAuditoriumSeatingRepository = uriAuditoriumSeatingRepository;
    }

    @Override
    public AuditoriumDto getAuditoriumSeatingFor(String showId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(uriAuditoriumSeatingRepository + "/api/data_for_auditoriumSeating/"+showId, String.class);
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        try {
            return mapper.readValue(response.getBody(), AuditoriumDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
