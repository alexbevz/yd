package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "type_courier_id")
    private TypeCourier typeCourierId;

    @ManyToMany
    @JoinTable(name = "courier_time_period", joinColumns = @JoinColumn(name = "courier_id"), inverseJoinColumns = @JoinColumn(name = "time_period_id"))
    private List<TimePeriod> timePeriodList;

    @ManyToMany
    @JoinTable(name = "courier_region", joinColumns = @JoinColumn(name = "courier_id"), inverseJoinColumns = @JoinColumn(name = "region_id"))
    private List<Region> regionList;
}
