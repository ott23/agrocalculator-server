package net.tngroup.acserver.database.h2.services;

import net.tngroup.acserver.database.h2.models.CalculatorStatus;

import java.util.List;

public interface CalculatorStatusService {

    List<CalculatorStatus> getByCalculatorId(int id);

    void save(CalculatorStatus calculatorStatus);

}
