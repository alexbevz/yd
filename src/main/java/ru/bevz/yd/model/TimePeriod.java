package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "time_period")
public class TimePeriod {

    @Id
    @Column(name = "time_period_id")
    private long timePeriodId;

    @Column(name = "left_limit")
    private LocalTime leftLimit;

    @Column(name = "right_limit")
    private LocalTime rightLimit;

}
