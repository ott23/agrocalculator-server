package net.tngroup.acserver.models;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calculator_id")
    private Calculator calculator;

}
