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
@Table(name = "courier")
public class Courier {

    @Id
    @Column(name = "courier_id")
    private long courierId;

    @Column(name = "type_courier_id")
    private long typeCourierId;

}
