package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.models.Unit;
import net.tngroup.acserver.databases.cassandra.service.ClientService;
import net.tngroup.acserver.databases.cassandra.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static net.tngroup.acserver.web.controllers.Responses.*;

@RestController
@RequestMapping("/unit")
public class UnitController {

    private UnitService unitService;
    private ClientService clientService;

    @Autowired
    public UnitController(UnitService unitService,
                          ClientService clientService) {
        this.unitService = unitService;
        this.clientService = clientService;
    }

    @RequestMapping
    public ResponseEntity getList() {
        try {
            List<Unit> unitList = unitService.getAll();
            return okResponse(unitList);
        } catch (JsonProcessingException e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/save")
    public ResponseEntity save(@RequestBody String jsonRequest) {
        try {
            Unit unit = new ObjectMapper().readValue(jsonRequest, Unit.class);

            List<Unit> unitList = unitService.getAllByNameOrImei(unit.getName(), unit.getImei());
            if (unitList.size() == 1 && !unitList.get(0).getId().equals(unit.getId()) || unitList.size() > 1) return conflictResponse();

            Client client = clientService.getById(unit.getClient());
            if (client == null) return failedDependencyResponse();

            if (unit.getId() == null) unit.setId(UUID.randomUUID());

            unitService.save(unit);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable UUID id) {
        try {
            unitService.deleteById(id);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }
}
