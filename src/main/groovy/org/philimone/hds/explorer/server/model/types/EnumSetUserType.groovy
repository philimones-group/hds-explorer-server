package org.philimone.hds.explorer.server.model.types

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.StringType
import org.hibernate.usertype.UserType

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class EnumSetUserType implements UserType {

    private Class<? extends Enum> enumClass

    EnumSetUserType(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass
    }

    @Override
    int[] sqlTypes() {
        return [StringType.INSTANCE.sqlType()] as int[]
    }

    @Override
    Class returnedClass() {
        return Set
    }

    Class<? extends Enum> getEnumClass() {
        return this.enumClass
    }

    @Override
    boolean equals(Object x, Object y) {
        return Objects.equals(x, y)
    }

    @Override
    int hashCode(Object x) {
        return x?.hashCode() ?: 0
    }

    @Override
    Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String csv = StringType.INSTANCE.get(rs, names[0], session)
        if (!csv) return new HashSet()

        Set<Enum> enumSet = new HashSet<>()
        csv.split(",").each { code ->
            def enumVal = enumClass.getMethod("getFrom", String).invoke(null, code)
            if (enumVal != null) {
                enumSet << enumVal
            }
        }
        return enumSet
    }

    @Override
    void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            StringType.INSTANCE.set(st, null, index, session)
        } else {
            def enumSet = (Set<Enum>) value
            def str = enumSet.collect { it.code }.join(',')
            StringType.INSTANCE.set(st, str, index, session)
        }
    }

    @Override
    Object deepCopy(Object value) {
        return value ? new HashSet(value) : null
    }

    @Override
    boolean isMutable() {
        return true
    }

    @Override
    Serializable disassemble(Object value) {
        return (Serializable) deepCopy(value)
    }

    @Override
    Object assemble(Serializable cached, Object owner) {
        return deepCopy(cached)
    }

    @Override
    Object replace(Object original, Object target, Object owner) {
        return deepCopy(original)
    }
}
