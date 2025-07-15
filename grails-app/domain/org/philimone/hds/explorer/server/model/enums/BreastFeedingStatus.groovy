package org.philimone.hds.explorer.server.model.enums

enum BreastFeedingStatus {

    EXCLUSIVE         ("EXCLUSIVE", "breastFeedingStatus.exclusive"),
    PARTIAL           ("PARTIAL", "breastFeedingStatus.partial"),
    NOT_BREASTFEEDING ("NOT_BREASTFEEDING", "breastFeedingStatus.not_breatfeeding")

    String code
    String name

    BreastFeedingStatus(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    @Override
    String toString() {
        return name
    }

    /* Finding Enum by code */
    private static final Map<String, BreastFeedingStatus> MAP = new HashMap<>()

    static {
        for (BreastFeedingStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    static BreastFeedingStatus getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}