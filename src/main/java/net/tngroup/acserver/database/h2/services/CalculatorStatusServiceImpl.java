package net.tngroup.acserver.database.h2.services;

import net.tngroup.acserver.database.h2.models.CalculatorStatus;
import net.tngroup.acserver.database.h2.repositories.CalculatorStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculatorStatusServiceImpl implements CalculatorStatusService {

    private CalculatorStatusRepository calculatorStatusRepository;

    @Autowired
    public CalculatorStatusServiceImpl(CalculatorStatusRepository calculatorStatusRepository) {
        this.calculatorStatusRepository = calculatorStatusRepository;
    }

    @Override
    public List<CalculatorStatus> getByCalculatorId(int id) {
        Pageable pageable = PageRequest.of(0, 20, Sort.Direction.DESC, "dateTime");
        return calculatorStatusRepository.findAllByCalculatorId(id, pageable);
    }

    @Override
    public void save(CalculatorStatus calculatorStatus) {
        this.calculatorStatusRepository.save(calculatorStatus);
    }
}
