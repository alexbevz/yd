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
@Table(name = "courier_region")
public class CourierRegion {

    @Id
    @Column(name = "courier_region_id")
    private long courierRegionId;

    @Column(name = "courier_id")
    private long courierId;

    @Column(name = "region_id")
    private long regionId;

}
