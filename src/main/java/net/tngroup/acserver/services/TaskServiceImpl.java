package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Task;
import net.tngroup.acserver.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllByConfirmed(boolean confirmed) {
        return taskRepository.findAllByConfirmed(confirmed);
    }

    @Override
    public void updateConfirmedById(int id, boolean confirmed) {
        taskRepository.findById(id).ifPresent(t -> {
            t.setConfirmed(confirmed);
            taskRepository.save(t);
        });
    }

    @Override
    public void add(Task task) {
        taskRepository.save(task);
    }
}
