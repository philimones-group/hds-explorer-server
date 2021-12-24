package org.philimone.hds.explorer.server.model.types

import grails.gorm.dirty.checking.DirtyCheck
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.StringType
import org.hibernate.usertype.UserType
import org.philimone.hds.explorer.server.model.main.Module

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class StringCollectionType implements UserType {

    @Override
    int[] sqlTypes() {
        return [StringType.INSTANCE.sqlType()] as int[]
    }

    @Override
    Class returnedClass() {
        return HashSet.class
    }

    @Override
    boolean equals(Object x, Object y) throws HibernateException {

        if (x instanceof HashSet<String> && y instanceof HashSet<String>){
            def listx = (HashSet<String>) x;
            def listy = (HashSet<String>) y;
            for (String s : listx) {
                if (!listy.contains(s)) {
                    return false
                }
            }

            return listy.size()==listx.size()

        } else {
            return false
        }
    }

    @Override
    int hashCode(Object x) throws HibernateException {
        assert (x != null)
        return x.hashCode()
    }

    @Override
    Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        assert names.length == 1;

        String moduleListText = StringType.INSTANCE.get(rs, names[0], session); // already handles null check

        //println ">>nullSafeGet(name=${names},${names[0]},"+moduleListText

        if (moduleListText == null || moduleListText.isEmpty()) return new HashSet<String>()

        def list = new HashSet<String>()
        list.addAll(moduleListText.split(","))
        return list
    }

    @Override
    void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {

        //println("<<nullSafeSet=${value}")

        if ( value == null ) {
            StringType.INSTANCE.set(st, null, index, session);
        } else {
            def list = (HashSet<String>) value;
            def str = null as String
            list.each {
                str = (str==null ? "":str+",") + it
            }

            StringType.INSTANCE.set(st, str, index, session)
        }
    }

    @Override
    Object deepCopy(Object value) throws HibernateException {
        return value
    }

    @Override
    boolean isMutable() {
        return true
    }

    @Override
    Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value
    }

    @Override
    Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached
    }

    @Override
    Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original
    }
}
