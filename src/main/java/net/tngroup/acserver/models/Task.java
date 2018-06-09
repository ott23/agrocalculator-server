package net.tngroup.acserver.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@Entity
@Table
public class Task {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @NonNull
    private Calculator calculator;

    @Column
    @NonNull
    private String type;

    @Column
    @NonNull
    private String value;

    @Column
    private boolean isConfirmed = false;
}
