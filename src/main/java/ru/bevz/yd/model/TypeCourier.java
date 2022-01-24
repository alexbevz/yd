package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "type_courier")
public class TypeCourier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "capacity")
    private float capacity;

    @Column(name = "profit_ratio")
    private float profitRatio;

}
