package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.CalculatorStatus;
import net.tngroup.acserver.models.Task;
import net.tngroup.acserver.repositories.CalculatorRepository;
import net.tngroup.acserver.repositories.CalculatorStatusRepository;
import net.tngroup.acserver.repositories.TaskRepository;
import net.tngroup.acserver.services.CipherService;
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

    private CalculatorRepository calculatorRepository;
    private CalculatorStatusRepository calculatorStatusRepository;
    private TaskRepository taskRepository;

    @Autowired
    public CalculatorController(CalculatorRepository calculatorRepository, CalculatorStatusRepository calculatorStatusRepository, TaskRepository taskRepository) {
        this.calculatorRepository = calculatorRepository;
        this.calculatorStatusRepository = calculatorStatusRepository;
        this.taskRepository = taskRepository;
    }

    @RequestMapping
    public String getList() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Calculator> calculatorList = calculatorRepository.findAll();
            return objectMapper.writeValueAsString(calculatorList);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/status/{id}")
    public String getStatusList(@PathVariable int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Calculator calculator = calculatorRepository.findCalculatorById(id);
            List<CalculatorStatus> calculatorStatusList = calculatorStatusRepository.findTop50ByCalculatorOrderByDateTimeDesc(calculator);
            return objectMapper.writeValueAsString(calculatorStatusList);
        } catch (JsonProcessingException e) {
            ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/need-key")
    public String getListNeedKey() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Calculator> calculatorList = calculatorRepository.findAllByNeedKey(true);
            return objectMapper.writeValueAsString(calculatorList);
        } catch (Exception e) {
            ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
            jsonResponse.put("response", "Server error: " + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping("/send-key/{id}")
    public String sendNewKeyById(@PathVariable int id) {
        ObjectNode jsonResponse = new ObjectMapper().createObjectNode();
        try {
            // Get calculator
            Calculator calculator = calculatorRepository.findCalculatorById(id);
            if (calculator == null) throw new Exception("Calculator not found");
            if (!calculator.isNeedKey()) throw new Exception("Calculator does not need key");

            // Generate calculator
            calculator.setKey(CipherService.generateDesKey());
            calculator.setNeedKey(false);
            calculatorRepository.save(calculator);

            // Make a task
            Task task = new Task(calculator, "key", calculator.getKey());
            taskRepository.save(task);

            jsonResponse.put("response", "Success");
        } catch (Exception e) {
            jsonResponse.put("response", "Server error: " + e.getMessage());
        }
        return jsonResponse.toString();
    }

}
