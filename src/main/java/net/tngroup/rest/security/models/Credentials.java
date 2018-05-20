package net.tngroup.rest.security.models;

import lombok.Data;

@Data
public class Credentials {
    private String username;
    private String password;
}