package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.components.CipherComponent;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.CalculatorStatus;
import net.tngroup.acserver.models.Task;
import net.tngroup.acserver.services.CalculatorService;
import net.tngroup.acserver.services.CalculatorStatusService;
import net.tngroup.acserver.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping
    public ResponseEntity getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Calculator> calculatorList = calculatorService.getAll();
            String response = objectMapper.writeValueAsString(calculatorList);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/status/{id}")
    public ResponseEntity getStatusList(@PathVariable int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<CalculatorStatus> calculatorStatusList = calculatorStatusService.getByCalculatorId(id);
            String response = objectMapper.writeValueAsString(calculatorStatusList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/need-key")
    public ResponseEntity getListNeedKey() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Calculator> calculatorList = calculatorService.getAllByKey(false);
            String response = objectMapper.writeValueAsString(calculatorList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/send-key/{id}")
    public ResponseEntity sendNewKeyById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            calculatorService.updateEncodedKeyById(id, CipherComponent.generateDesKey());
            Calculator calculator = calculatorService.getById(id);
            taskService.add(new Task(calculator, calculator.getName(), calculator.getEncodedKey()));
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/switch/{id}")
    public ResponseEntity switchById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            Calculator calculator = calculatorService.getById(id);
            taskService.add(new Task(calculator, "command", calculator.isStatus() ? "stop" : "start"));
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/shutdown/{id}")
    public ResponseEntity shutdownById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            Calculator calculator = calculatorService.getById(id);
            taskService.add(new Task(calculator, "command", "shutdown"));
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/kill/{id}")
    public ResponseEntity killById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            Calculator calculator = calculatorService.getById(id);
            taskService.add(new Task(calculator, "command", "destroy"));
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            Calculator calculator = calculatorService.getById(id);
            calculatorService.removeById(calculator.getId());
            jsonResponse.put("response", "Success");
            String response = jsonResponse.toString();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
            String response = jsonResponse.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
