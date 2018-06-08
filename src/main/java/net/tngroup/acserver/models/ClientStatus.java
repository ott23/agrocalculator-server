package net.tngroup.acserver.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Entity
@Table
public class ClientStatus {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column
    @NonNull
    private String status;

    @Column
    @NonNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
}
