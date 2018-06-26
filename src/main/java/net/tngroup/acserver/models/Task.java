package net.tngroup.acserver.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "calculator_id", nullable = false)
    private Calculator calculator;

    @NonNull
    private String type;

    @NonNull
    @Column(length = 5000)
    private String value;

    private boolean confirmed = false;
}
