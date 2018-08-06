package net.tngroup.acserver.databases.h2.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.net.InetSocketAddress;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"tasks", "nodeStatuses", "settings"})
@ToString(exclude = {"tasks", "nodeStatuses", "settings"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Node.class)
@Entity
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;

    private String code;

    private String name;

    private InetSocketAddress address;

    private String key;

    private boolean status = false;

    private boolean connection = false;

    private boolean archive = false;

    //@JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
    private Set<Task> tasks;

    //@JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
    private Set<NodeStatus> nodeStatuses;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
    private Set<Setting> settings;
}
