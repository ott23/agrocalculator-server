package net.tngroup.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.rest.models.Person;
import net.tngroup.rest.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @RequestMapping
    public String getList() {
        String response;
        try {
            List<Person> personList = personRepository.findAll();
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.writeValueAsString(personList);
        } catch (JsonProcessingException e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/get/{id}")
    public String getById(@PathVariable int id) {
        String response;
        try {
            if (personRepository.findById(id).isPresent()) {
                ObjectMapper mapper = new ObjectMapper();
                Person person = personRepository.findById(id).get();
                response = mapper.writeValueAsString(person);
            } else {
                throw new Exception("Person not found");
            }
        } catch (Exception e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/add")
    public String add(@RequestBody String jsonRequest) {
        String response;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Person person = mapper.readValue(jsonRequest, Person.class);
            person = personRepository.save(person);
            response = mapper.writeValueAsString(person);
        } catch (Exception e) {
            response = "Server error";
        }
        return response;
    }

    @RequestMapping("/delete/{id}")
    public void deleteById(@PathVariable int id) {
        personRepository.deleteById(id);
    }

}
