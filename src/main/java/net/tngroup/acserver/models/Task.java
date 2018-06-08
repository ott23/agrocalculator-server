package net.tngroup.acserver.models;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Data
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
    private Client client;

    @Column
    @NonNull
    private String type;

    @Column
    private String value;

    @Column
    private boolean isConfirmed;
}
