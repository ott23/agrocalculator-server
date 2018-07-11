package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.database.h2.models.Calculator;
import net.tngroup.acserver.database.h2.models.CalculatorStatus;
import net.tngroup.acserver.database.h2.models.Task;
import net.tngroup.acserver.database.h2.services.CalculatorService;
import net.tngroup.acserver.database.h2.services.CalculatorStatusService;
import net.tngroup.acserver.database.h2.services.TaskService;
import net.tngroup.acserver.web.components.CipherComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private CalculatorService calculatorService;
    private CalculatorStatusService calculatorStatusService;
    private TaskService taskService;

    @Autowired
    public CalculatorController(CalculatorService calculatorService,
                                CalculatorStatusService calculatorStatusService,
                                TaskService taskService) {
        this.calculatorService = calculatorService;
        this.calculatorStatusService = calculatorStatusService;
        this.taskService = taskService;
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

    @RequestMapping
    public ResponseEntity getList() {
        try {
            List<Calculator> calculatorList = calculatorService.getAll();
            return okResponse(calculatorList);
        } catch (JsonProcessingException e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/getAllByName/{name}")
    public ResponseEntity getByName(@PathVariable String name) {
        try {
            List<Calculator> calculatorList = calculatorService.getAllByName(name);
            return okResponse(calculatorList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/status/{id}")
    public ResponseEntity getStatusList(@PathVariable int id) {
        try {
            List<CalculatorStatus> calculatorStatusList = calculatorStatusService.getByCalculatorId(id);
            return okResponse(calculatorStatusList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/sendKey/{id}")
    public ResponseEntity sendNewKeyById(@PathVariable int id) {
        try {
            calculatorService.updateKeyById(id, CipherComponent.generateDesKey());
            Calculator calculator = calculatorService.getById(id);
            taskService.save(new Task(calculator, "key", calculator.getKey()));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/set")
    public ResponseEntity set(@RequestBody String jsonRequest) {
        try {
            Calculator calculator = new ObjectMapper().readValue(jsonRequest, Calculator.class);
            calculatorService.save(calculator);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/switch/{id}")
    public ResponseEntity switchById(@PathVariable int id) {
        try {
            Calculator calculator = calculatorService.getById(id);
            taskService.save(new Task(calculator, "command", calculator.isStatus() ? "stop" : "start"));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/shutdown/{id}")
    public ResponseEntity shutdownById(@PathVariable int id) {
        try {
            Calculator calculator = calculatorService.getById(id);
            taskService.save(new Task(calculator, "command", "shutdown"));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/kill/{id}")
    public ResponseEntity killById(@PathVariable int id) {
        try {
            Calculator calculator = calculatorService.getById(id);
            taskService.save(new Task(calculator, "command", "destroy"));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable int id) {
        try {
            Calculator calculator = calculatorService.getById(id);
            calculatorService.removeById(calculator.getId());
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

}
