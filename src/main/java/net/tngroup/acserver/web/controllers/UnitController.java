package net.tngroup.acserver.web.controllers;

import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.models.Unit;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import net.tngroup.acserver.databases.cassandra.services.UnitService;
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
@Lazy
@RequestMapping("/unit")
public class UnitController {

    private UnitService unitService;
    private ClientService clientService;

    public UnitController(@Lazy UnitService unitService,
                          @Lazy ClientService clientService) {
        this.unitService = unitService;
        this.clientService = clientService;
    }

    @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {

        List<Unit> unitList = unitService.getAll();
        return okResponse(unitList);
    }

    @RequestMapping("/save")
    public ResponseEntity save(HttpServletRequest request, @RequestBody Unit unit) {

        List<Unit> unitList = unitService.getAllByImei(unit.getImei());
        if (unitList.size() == 1 && !unitList.get(0).getId().equals(unit.getId()) || unitList.size() > 1)
            return conflictResponse("imei");

        final Client client = clientService.getById(unit.getClient());
        if (client == null) return failedDependencyResponse();

        if (unit.getId() == null) unit.setId(UUID.randomUUID());

        unitService.save(unit);
        return successResponse();

    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable UUID id) {

        unitService.deleteById(id);
        return successResponse();
    }
}
