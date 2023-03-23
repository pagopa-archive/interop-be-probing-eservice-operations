package it.pagopa.interop.probing.eservice.operations.model;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class CustomStringArrayType implements UserType {
	@Override
	public int[] sqlTypes() {
		return new int[] { Types.ARRAY };
	}

	@Override
	public Class returnedClass() {
		return String[].class;
	}

	@Override
	public boolean equals(Object o, Object o1) throws HibernateException {
		return Objects.equals(o, o1);
	}

	@Override
	public int hashCode(Object o) throws HibernateException {
		return o.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		Array array = rs.getArray(names[0]);
		return array != null ? array.getArray() : null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if (st != null) {
			if (value != null) {
				Array array = session.connection().createArrayOf("varchar", (String[]) value);
				st.setArray(index, array);
			} else {
				st.setNull(index, sqlTypes()[0]);
			}
		}
	}

	@Override
	public Object deepCopy(Object o) throws HibernateException {
		return SerializationUtils.clone((String[]) o);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object o) throws HibernateException {
		return (Serializable) o;
	}

	@Override
	public Object assemble(Serializable serializable, Object o) throws HibernateException {
		return serializable;
	}

	@Override
	public Object replace(Object o, Object o1, Object o2) throws HibernateException {
		return o;
	}
}
