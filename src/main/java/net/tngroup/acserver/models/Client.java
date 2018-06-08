package net.tngroup.acserver.models;

import lombok.Data;
import lombok.NonNull;

import javax.crypto.SecretKey;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table
public class Client {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NonNull
    private String address;

    @Column
    private SecretKey key;

    @Column
    private boolean isAccepted;

    @Column
    private boolean isConfirmed;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastStatus;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="client")
    private List<ClientStatus> statuses;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="client")
    private List<Task> tasks;



}
