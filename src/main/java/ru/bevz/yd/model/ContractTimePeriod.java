package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "contract_time_period")
public class ContractTimePeriod {

    @Id
    @Column(name = "contract_time_period_id")
    private long contractTimePeriodId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "time_period_id")
    private TimePeriod timePeriod;

}
