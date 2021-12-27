package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "type_courier")
public class TypeCourier {

    @Id
    @Column(name = "type_courier_id")
    private long typeCourierId;

    @Column(name = "name_type")
    private String nameType;

    @Column
    private float capacity;

    @Column(name = "profit_ratio")
    private float profitRatio;

}
