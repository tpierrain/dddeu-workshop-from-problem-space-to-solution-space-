package com.baasie.ExternalDependencies.auditoriumlayoutrepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AuditoriumLayoutRepository {

    private Map<String, AuditoriumDto> repository = new HashMap<>();

    public AuditoriumLayoutRepository() throws IOException {
        String jsonDirectory = Paths.get(System.getProperty("user.dir")).getParent().getParent().getParent().toString() + "/Stubs/AuditoriumLayouts";

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
        if (repository.containsKey(showId))
        {
            return repository.get(showId);
        }

        return new AuditoriumDto();
    }
}
