package net.tngroup.acserver.databases.h2.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope = CalculatorStatus.class)
@Entity
public class CalculatorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "calculator_id", nullable = false)
    private Calculator calculator;

    public CalculatorStatus(String status, Date dateTime, Calculator calculator) {
        this.status = status;
        this.dateTime = dateTime;
        this.calculator = calculator;
    }
}
