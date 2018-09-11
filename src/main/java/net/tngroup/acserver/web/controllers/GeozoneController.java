package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.models.Geozone;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import net.tngroup.acserver.databases.cassandra.services.GeozoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

import static net.tngroup.acserver.web.controllers.Responses.*;

@RestController
@RequestMapping("/geozone")
public class GeozoneController {

    private GeozoneService geozoneService;
    private ClientService clientService;

    @Autowired
    public GeozoneController(@Lazy ClientService clientService,
                             @Lazy GeozoneService geozoneService) {
        this.geozoneService = geozoneService;
        this.clientService = clientService;
    }

    @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {
        try {
            List<Geozone> geozoneList = geozoneService.getAll();
            return okResponse(geozoneList);
        } catch (JsonProcessingException e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/save")
    public ResponseEntity save(HttpServletRequest request, @RequestBody Geozone geozone) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            try {
                objectMapper.readTree(geozone.getGeometry());
            } catch (Exception e) {
                throw new Exception("Json not valid");
            }

            Client client = clientService.getById(geozone.getClient());
            if (client == null) return failedDependencyResponse();
            if (geozone.getId() == null) geozone.setId(UUID.randomUUID());

            geozoneService.save(geozone);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable UUID id) {
        try {
            geozoneService.deleteById(id);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }


}
