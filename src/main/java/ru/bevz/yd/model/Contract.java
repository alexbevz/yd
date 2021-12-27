package ru.bevz.yd.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "region_id")
    private long regionId;

    @Column
    private float weight;

    @Column
    @Enumerated(EnumType.STRING)
    @Type(type = "status_contract")
    private StatusContract status;

    @Column(name = "courier_id")
    private long courierId;

    @Column(name = "datetime_assignment")
    private LocalDateTime datetimeAssignment;

    @Column(name = "datetime_realization")
    private LocalDateTime datetimeRealization;
}
