package net.tngroup.acserver.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class CalculatorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "calculator_id", nullable = false)
    private Calculator calculator;

    @NonNull
    private String status;

    @NonNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
}
