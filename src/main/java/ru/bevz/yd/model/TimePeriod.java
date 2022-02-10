package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "time_period")
public class TimePeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "left_limit")
    private LocalTime from;

    @Column(name = "right_limit")
    private LocalTime to;

}
