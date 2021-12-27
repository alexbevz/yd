package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "contract")
@TypeDef(name = "status_contract", typeClass = PostgreSQLStatusContract.class)
public class Contract {

    @Id
    @Column(name = "contract_id")
    private long contractId;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @Column
    private float weight;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Type(type = "status_contract")
    private StatusContract status;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @Column(name = "datetime_assignment")
    private LocalDateTime datetimeAssignment;

    @Column(name = "datetime_realization")
    private LocalDateTime datetimeRealization;

    @ManyToMany
    @JoinTable(name = "contract_time_period", joinColumns = @JoinColumn(name = "contract_id"), inverseJoinColumns = @JoinColumn(name = "time_period_id"))
    private List<TimePeriod> TimePeriodList;

}
