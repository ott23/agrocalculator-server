package net.tngroup.acserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.databases.h2.models.Node;
import net.tngroup.acserver.databases.h2.models.NodeStatus;
import net.tngroup.acserver.databases.h2.models.Task;
import net.tngroup.acserver.databases.h2.services.NodeService;
import net.tngroup.acserver.databases.h2.services.NodeStatusService;
import net.tngroup.acserver.databases.h2.services.TaskService;
import net.tngroup.acserver.web.components.CipherComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static net.tngroup.acserver.web.controllers.Responses.*;

@CrossOrigin
@RestController
@RequestMapping("/node")
public class NodeController {

    private NodeService nodeService;
    private NodeStatusService nodeStatusService;
    private TaskService taskService;

    public NodeController(NodeService nodeService,
                          NodeStatusService nodeStatusService,
                          TaskService taskService) {
        this.nodeService = nodeService;
        this.nodeStatusService = nodeStatusService;
        this.taskService = taskService;
    }

        @RequestMapping
    public ResponseEntity getList(HttpServletRequest request) {
        try {
            List<Node> nodeList = nodeService.getAll();
            return okResponse(nodeList);
        } catch (JsonProcessingException e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/getAllByName/{name}")
    public ResponseEntity getByName(HttpServletRequest request, @PathVariable String name) {
        try {
            List<Node> nodeList = nodeService.getAllByName(name);
            return okResponse(nodeList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/status/{id}")
    public ResponseEntity getStatusList(HttpServletRequest request, @PathVariable int id) {
        try {
            List<NodeStatus> nodeStatusList = nodeStatusService.getByCalculatorId(id);
            return okResponse(nodeStatusList);
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/sendKey/{id}")
    public ResponseEntity sendNewKeyById(HttpServletRequest request, @PathVariable int id) {
        try {
            nodeService.updateKeyById(id, CipherComponent.generateDesKey());
            Node node = nodeService.getById(id);
            taskService.save(new Task(node, "key", node.getKey()));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/set")
    public ResponseEntity set(HttpServletRequest request, @RequestBody Node node) {
        try {
            nodeService.save(node);
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/switch/{id}")
    public ResponseEntity switchById(HttpServletRequest request, @PathVariable int id) {
        try {
            Node node = nodeService.getById(id);
            taskService.save(new Task(node, "command", node.isStatus() ? "stop" : "start"));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/shutdown/{id}")
    public ResponseEntity shutdownById(HttpServletRequest request, @PathVariable int id) {
        try {
            Node node = nodeService.getById(id);
            taskService.save(new Task(node, "command", "shutdown"));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/kill/{id}")
    public ResponseEntity killById(HttpServletRequest request, @PathVariable int id) {
        try {
            Node node = nodeService.getById(id);
            taskService.save(new Task(node, "command", "destroy"));
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

    @RequestMapping("/delete/{id}")
    public ResponseEntity deleteById(HttpServletRequest request, @PathVariable int id) {
        try {
            Node node = nodeService.getById(id);
            nodeService.removeById(node.getId());
            return successResponse();
        } catch (Exception e) {
            return badResponse(e);
        }
    }

}
