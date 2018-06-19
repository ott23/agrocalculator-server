package net.tngroup.acserver.services;

import net.tngroup.acserver.models.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllByConfirmed(boolean confirmed);

    void updateConfirmedById(int id, boolean confirmed);

    void add(Task task);
}
