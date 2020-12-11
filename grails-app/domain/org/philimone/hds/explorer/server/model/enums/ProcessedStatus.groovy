package org.philimone.hds.explorer.server.model.enums

/**
 * A Log Status represents four different states of a task, that can be (Started, Finished, With Error and Not Started)
 */
enum ProcessedStatus {

    NOT_PROCESSED (0,"processedStatus.not_processed.label"),
    SUCCESS (1, "processedStatus.success.label"),
    ERROR (2, "processedStatus.error.label")

    Integer code
    String name

    ProcessedStatus(Integer code, String name){
        this.code = code
        this.name = name
    }

    Integer getId(){
        code
    }

    String toString(){
        name
    }

    /* Finding Enum by code */
    private static final Map<Integer, ProcessedStatus> MAP = new HashMap<>();

    static {
        for (ProcessedStatus e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static ProcessedStatus getFrom(Integer code) {
        return code==null ? null : MAP.get(code);
    }
}
