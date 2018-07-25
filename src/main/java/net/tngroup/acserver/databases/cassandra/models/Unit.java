package net.tngroup.acserver.databases.cassandra.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Unit {

    @PrimaryKey
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private String imei;

    @NonNull
    private UUID client;

}
