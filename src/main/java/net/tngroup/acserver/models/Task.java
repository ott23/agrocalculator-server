package net.tngroup.acserver.models;

import lombok.*;

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
    @ManyToOne
    @JoinColumn(name = "calculator_id", nullable = false)
    private Calculator calculator;

    @NonNull
    private String type;

    @NonNull
    private String value;

    private boolean confirmed = false;
}
