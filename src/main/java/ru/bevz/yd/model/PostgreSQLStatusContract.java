package ru.bevz.yd.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PostgreSQLStatusContract extends EnumType<StatusContract> {

    @Override
    public void nullSafeSet(
            PreparedStatement st,
            Object value, int index,
            SharedSessionContractImplementor session
    ) throws HibernateException, SQLException {
        st.setObject(
                index,
                value != null ? ((StatusContract) value).name() : null,
                Types.OTHER
        );
    }
}
