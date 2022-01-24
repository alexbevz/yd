package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "time_period")
public class TimePeriod {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "left_limit")
    private LocalTime from;

    @Column(name = "right_limit")
    private LocalTime to;

}
