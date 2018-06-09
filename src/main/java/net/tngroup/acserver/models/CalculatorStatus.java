package net.tngroup.acserver.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Entity
@Table
public class CalculatorStatus {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "calculator_id", nullable = false)
    @NonNull
    private Calculator calculator;

    @Column
    @NonNull
    private String status;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @NonNull
    private Date dateTime;
}
