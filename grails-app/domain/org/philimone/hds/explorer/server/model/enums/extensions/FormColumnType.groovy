package org.philimone.hds.explorer.server.model.enums.extensions

enum FormColumnType {

    UNSUPPORTED     (-1),
    NULL            (0),
    TEXT            (1),
    INTEGER         (2),
    DECIMAL         (3),
    DATE            (4),
    TIME            (5),
    DATE_TIME       (6),
    CHOICE          (7),
    MULTIPLE_ITEMS  (8),
    BOOLEAN         (9),
    GEOPOINT        (10),
    BARCODE         (11),
    BINARY          (12),
    LONG            (13),
    GEOSHAPE        (14),
    GEOTRACE        (15),
    REPEAT_GROUP    (40);


    Integer code
    String name

    FormColumnType(Integer code){
        this.code = code
        //this.name = name
    }

    String getId(){
        return name()
    }

    @Override
    String toString() {
        return name()
    }

    /* Finding Enum by code */
    private static final Map<Integer, FormColumnType> MAP = new HashMap<>()

    static {
        for (FormColumnType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static FormColumnType getFrom(Integer code) {
        return code==null ? null : MAP.get(code)
    }
}