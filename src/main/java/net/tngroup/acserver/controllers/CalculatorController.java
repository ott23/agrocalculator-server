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
    public String getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Calculator> calculatorList = calculatorService.getAll();
            return objectMapper.writeValueAsString(calculatorList);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/status/{id}")
    public String getStatusList(@PathVariable int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<CalculatorStatus> calculatorStatusList = calculatorStatusService.getByCalculatorId(id);
            return objectMapper.writeValueAsString(calculatorStatusList);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/need-key")
    public String getListNeedKey() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Calculator> calculatorList = calculatorService.getAllByKey(null);
            return objectMapper.writeValueAsString(calculatorList);
        } catch (Exception e) {
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/send-key/{id}")
    public String sendNewKeyById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            calculatorService.updateKeyById(id, CipherComponent.generateDesKey());
            Calculator calculator = calculatorService.getById(id);
            taskService.add(new Task(calculator, "key", calculator.getKey()));
            jsonResponse.put("response", "Success");
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
        }
        return jsonResponse.toString();
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            calculatorService.updateArchiveById(id, true);
            Calculator calculator = calculatorService.getById(id);
            taskService.add(new Task(calculator, "command", "destroy"));
            jsonResponse.put("response", "Success");
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
        }
        return jsonResponse.toString();
    }

}
