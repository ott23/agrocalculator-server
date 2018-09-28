package net.tngroup.acserver.web.controllers;

import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.models.Geozone;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import net.tngroup.acserver.databases.cassandra.services.GeozoneService;
import net.tngroup.common.json.JsonComponent;
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

import static net.tngroup.common.responses.Responses.*;


@RestController
@RequestMapping("/geozone")
public class GeozoneController {

    private GeozoneService geozoneService;
    private ClientService clientService;
    private JsonComponent jsonComponent;

    @Autowired
    public GeozoneController(@Lazy ClientService clientService,
                             @Lazy GeozoneService geozoneService,
                             JsonComponent jsonComponent) {
        this.geozoneService = geozoneService;
        this.clientService = clientService;
        this.jsonComponent = jsonComponent;
    }

    @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {

        final List<Geozone> geozoneList = geozoneService.getAll();
        return okResponse(geozoneList);
    }

    @RequestMapping("/save")
    public ResponseEntity save(HttpServletRequest request, @RequestBody Geozone geozone) throws Exception {


        try {
            if (geozone.getGeometry() != null){
                jsonComponent.getObjectMapper().readTree(geozone.getGeometry());
            }
        } catch (Exception e) {
            throw new Exception("Json not valid");
        }

        final Client client = clientService.getById(geozone.getClient());
        if (client == null) return failedDependencyResponse();
        if (geozone.getId() == null) geozone.setId(UUID.randomUUID());

        geozoneService.save(geozone);
        return successResponse();

    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable UUID id) {
        geozoneService.deleteById(id);
        return successResponse();

    }


}
