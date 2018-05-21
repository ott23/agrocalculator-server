package net.tngroup.rest.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table
public class Person {

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column
    @NonNull
    private String name;

    @Column
    @NonNull
    private Integer age;
}
