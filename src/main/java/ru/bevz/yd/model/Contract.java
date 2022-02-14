package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "\"order\"")
@TypeDef(name = "status_order", typeClass = PostgreSQLStatusContract.class)
public class Contract {

    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "weight")
    private float weight;

    @Enumerated(EnumType.STRING)
    @Type(type = "status_order")
    @Column(name = "status")
    private StatusContract status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_time_period",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "time_period_id")
    )
    private Set<TimePeriod> timePeriods;

    @Column(name = "datetime_assignment")
    private LocalDateTime datetimeAssignment;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @ManyToOne
    @JoinColumn(name = "type_courier_id")
    private TypeCourier typeCourier;

    @Column(name = "datetime_realization_start")
    private LocalDateTime datetimeRealizationStart;

    @Column(name = "datetime_realization")
    private LocalDateTime datetimeRealization;

}
