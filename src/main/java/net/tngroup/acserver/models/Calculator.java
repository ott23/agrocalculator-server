package net.tngroup.acserver.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.SocketAddress;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table
public class Calculator {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private SocketAddress address;

    @Column
    private String key;

    @Column
    private boolean needKey = false;

    @Column
    private boolean active = false;

    @Column
    private boolean archive = false;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastStatus;

}
