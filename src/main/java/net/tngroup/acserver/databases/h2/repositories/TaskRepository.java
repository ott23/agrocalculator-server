package net.tngroup.acserver.databases.h2.repositories;

import net.tngroup.acserver.databases.h2.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByConfirmed(boolean confirmed);

}
