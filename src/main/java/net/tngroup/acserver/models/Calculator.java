package net.tngroup.acserver.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"tasks", "calculatorStatuses", "settings"})
@ToString(exclude = {"tasks", "calculatorStatuses", "settings"})
@Entity
public class Calculator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @JsonDeserialize(as = InetSocketAddress.class)
    private SocketAddress address;

    private String encodedKey;

    private boolean key = false;

    private boolean status = false;

    private boolean connection = false;

    private boolean archive = false;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculator")
    private Set<Task> tasks;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculator")
    private Set<CalculatorStatus> calculatorStatuses;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculator")
    private Set<Setting> settings;

}
