package org.philimone.hds.explorer.server.model.enums

enum IllnessSymptoms {

    FEVER              ("FEVER", "childIllnessSymptom.fever"),
    DIARRHEA           ("DIARRHEA", "childIllnessSymptom.diarrhea"),
    COUGH              ("COUGH", "childIllnessSymptom.cough"),
    BREATHING_PROBLEMS ("BREATHING_PROBLEMS", "childIllnessSymptom.breathing_problems"),
    RASH               ("RASH", "childIllnessSymptom.rash"),
    VOMITING           ("VOMITING", "childIllnessSymptom.vomiting"),
    EYE_DISCHARGE      ("EYE_DISCHARGE", "childIllnessSymptom.eye_discharge"),
    OTHER              ("OTHER", "childIllnessSymptom.other")

    String code
    String name

    IllnessSymptoms(String code, String name) {
        this.code = code
        this.name = name
    }

    String getId() {
        return code
    }

    private static final Map<String, IllnessSymptoms> MAP = new HashMap<>()

    static {
        for (IllnessSymptoms e: values()) {
            MAP.put(e.code, e)
        }
    }

    static IllnessSymptoms getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }

    @Override
    String toString() {
        return name
    }
}