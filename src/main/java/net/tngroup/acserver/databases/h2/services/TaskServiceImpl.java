package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.Task;
import net.tngroup.acserver.databases.h2.repositories.TaskRepository;
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
    public Task getById(int id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void updateConfirmedById(int id, boolean confirmed) {
        taskRepository.findById(id).ifPresent(t -> {
            t.setConfirmed(confirmed);
            taskRepository.save(t);
        });
    }

    @Override
    public void save(Task task) {
        taskRepository.save(task);
    }
}
