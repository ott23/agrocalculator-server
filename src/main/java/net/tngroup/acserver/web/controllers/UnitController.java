package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.database.cassandra.models.Client;
import net.tngroup.acserver.database.cassandra.models.Unit;
import net.tngroup.acserver.database.cassandra.service.ClientService;
import net.tngroup.acserver.database.cassandra.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    private ResponseEntity okResponse(Object o) throws JsonProcessingException {
        String response = new ObjectMapper().writeValueAsString(o);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity successResponse() {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        jsonResponse.put("response", "Success");
        String response = jsonResponse.toString();
        return ResponseEntity.ok(response);
    }

    private ResponseEntity badResponse(Exception e) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        jsonResponse.put("response", "Server error: " + e.getMessage());
        String response = jsonResponse.toString();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity conflictResponse() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    private ResponseEntity failedDependencyResponse() {
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
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
