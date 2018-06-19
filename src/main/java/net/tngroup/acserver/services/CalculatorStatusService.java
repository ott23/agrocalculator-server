package net.tngroup.acserver.services;

import net.tngroup.acserver.models.CalculatorStatus;

import java.util.List;

public interface CalculatorStatusService {

    List<CalculatorStatus> getByCalculatorId(int id);

    void add(CalculatorStatus calculatorStatus);

}
