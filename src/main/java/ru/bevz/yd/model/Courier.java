package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "courier")
public class Courier {

    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "type_courier_id")
    private TypeCourier typeCourier;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "courier_time_period",
            joinColumns = @JoinColumn(name = "courier_id"),
            inverseJoinColumns = @JoinColumn(name = "time_period_id")
    )
    private Set<TimePeriod> timePeriods;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "courier_region",
            joinColumns = @JoinColumn(name = "courier_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private Set<Region> regions;

}
