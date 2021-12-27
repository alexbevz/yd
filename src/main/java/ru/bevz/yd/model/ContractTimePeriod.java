package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    @Column(name = "contract_id")
    private long contractId;

    @Column(name = "time_period_id")
    private long timePeriodId;

}
