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
@Table(name = "courier_time_period")
public class CourierTimePeriod {

    @Id
    @Column(name = "courier_time_period_id")
    private long courierTimePeriodId;

    @Column(name = "courier_id")
    private long courierId;

    @Column(name = "period_time_id")
    private long periodTimeId;

}
