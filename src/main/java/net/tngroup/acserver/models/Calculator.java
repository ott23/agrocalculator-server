package net.tngroup.acserver.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Data
@Entity
public class Calculator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @JsonDeserialize(as = InetSocketAddress.class)
    private SocketAddress address;

    private String key;

    private boolean active = false;

    private boolean archive = false;

}
