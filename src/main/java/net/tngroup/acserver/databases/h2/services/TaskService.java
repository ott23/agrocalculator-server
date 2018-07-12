package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.Task;

import java.util.List;

public interface TaskService {

    List<Task> getAllByConfirmed(boolean confirmed);

    Task getById(int id);

    void updateConfirmedById(int id, boolean confirmed);

    void save(Task task);
}
