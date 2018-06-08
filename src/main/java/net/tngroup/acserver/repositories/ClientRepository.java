package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    Client findClientByAddress(String address);

    List<Client> findAllByIsAccepted(boolean accepted);
}
