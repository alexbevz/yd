package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @ManyToOne
    @JoinColumn(name = "time_period_id")
    private TimePeriod timePeriod;

}
