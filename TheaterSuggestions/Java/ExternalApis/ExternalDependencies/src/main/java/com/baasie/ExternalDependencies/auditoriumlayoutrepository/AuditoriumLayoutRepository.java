package com.baasie.ExternalDependencies.auditoriumlayoutrepository;

import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditoriumLayoutRepository implements IProvideAuditoriumLayouts {

    private Map<String, AuditoriumDto> repository = new HashMap<>();

    public AuditoriumLayoutRepository() throws IOException, URISyntaxException {
        String jsonDirectory = Paths.get(ClassLoader.getSystemResource("AuditoriumLayouts").toURI()).toString();
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(jsonDirectory));

        for (Path path : directoryStream) {
            if (path.toString().contains("_theater.json")) {
                String fileName = path.getFileName().toString();
                ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
                repository.put(fileName.split("-")[0], mapper.readValue(path.toFile(), AuditoriumDto.class));
            }
        }
    }

    public AuditoriumDto GetAuditoriumLayoutFor(String showId) {
        if (repository.containsKey(showId)) {
            return repository.get(showId);
        }
        return new AuditoriumDto();
    }

    public AuditoriumDto getAuditoriumSeatingFor(String showId) {
        if (repository.containsKey(showId)) {
            return repository.get(showId);
        }

        return new AuditoriumDto();
    }
}
