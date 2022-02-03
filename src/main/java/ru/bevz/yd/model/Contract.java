package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "contract")
@TypeDef(name = "status_contract", typeClass = PostgreSQLStatusContract.class)
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
    @Type(type = "status_contract")
    @Column(name = "status")
    private StatusContract status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "contract_time_period"
            , joinColumns = @JoinColumn(name = "contract_id")
            , inverseJoinColumns = @JoinColumn(name = "time_period_id")
    )
    private Set<TimePeriod> TimePeriodList;

    @Column(name = "datetime_assignment")
    private LocalDateTime datetimeAssignment;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @ManyToOne
    @Column(name = "type_courier_id")
    private TypeCourier typeCourier;

    @Column(name = "datetime_realization_start")
    private LocalDateTime datetimeRealizationStart;

    @Column(name = "datetime_realization")
    private LocalDateTime datetimeRealization;

}
