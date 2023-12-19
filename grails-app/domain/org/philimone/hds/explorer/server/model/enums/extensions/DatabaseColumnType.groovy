package org.philimone.hds.explorer.server.model.enums.extensions

enum DatabaseColumnType {

    NOT_APPLICABLE (-1),
    BLOB    (1), //Currently Living Here
    BOOLEAN (2),
    DECIMAL (3),
    DOUBLE (4),
    INTEGER (5),
    DATETIME (6),
    STRING (7)

    public Integer code
    public String name

    DatabaseColumnType(Integer code){
        this.code = code
        this.name = name()
    }

    public String getId(){
        return name()
    }

    @Override
    public String toString() {
        return name()
    }

    /* Finding Enum by code */
    private static final Map<Integer, DatabaseColumnType> MAP = new HashMap<>()

    static {
        for (DatabaseColumnType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static DatabaseColumnType getFrom(Integer code) {
        return code==null ? null : MAP.get(code)
    }
}