package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.h2.models.Calculator;
import net.tngroup.acserver.databases.h2.models.CalculatorStatus;
import net.tngroup.acserver.databases.h2.models.Task;
import net.tngroup.acserver.databases.h2.services.CalculatorService;
import net.tngroup.acserver.databases.h2.services.CalculatorStatusService;
import net.tngroup.acserver.databases.h2.services.TaskService;
import net.tngroup.acserver.web.components.CipherComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static net.tngroup.acserver.web.controllers.Responses.*;

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
