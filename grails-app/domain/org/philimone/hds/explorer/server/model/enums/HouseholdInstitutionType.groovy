package org.philimone.hds.explorer.server.model.enums

enum HouseholdInstitutionType {

    ORPHANAGE             ("ORP", "householdInstitutionType.orphanage", "Provide shelter and care for children without parental support. Managed by an administrator or director."),
    MENTAL_HEALTH_FACILITY ("MHF", "householdInstitutionType.mental_health_facility", "Care for individuals with mental health or social vulnerabilities. Managed by healthcare professionals or a director."),
    ELDERLY_CARE_HOME     ("ECH", "householdInstitutionType.elderly_care_home", "Accommodate senior citizens requiring support with daily living. Managed by staff."),
    BOARDING_SCHOOL       ("BSC", "householdInstitutionType.boarding_school", "Residential accommodation for school students. Managed by a principal or administrative staff."),
    UNIVERSITY_DORM       ("UDM", "householdInstitutionType.university_dorm", "Dormitories for university or college students. Managed by campus housing administrators."),
    HOSTEL                ("HST", "householdInstitutionType.hostel", "Temporary or semi-permanent housing for workers, students, or transient populations. Managed by a hostel warden or supervisor."),
    RELIGIOUS_INSTITUTION ("REL", "householdInstitutionType.religious_institution", "Communal living for members of religious orders. Typically led by a spiritual or administrative head."),
    REFUGEE_CAMP          ("RFC", "householdInstitutionType.refugee_camp", "Temporary shelter for displaced individuals or families. Managed by camp coordinators or aid organizations."),
    IDP_CAMP              ("IDC", "householdInstitutionType.idp_camp", "Shelter for internally displaced persons (IDPs). Managed by local or international agencies."),
    PRISON                ("PRI", "householdInstitutionType.prison", "Correctional facility for incarcerated individuals. Governed by prison authorities or wardens."),
    JUVENILE_DETENTION    ("JDT", "householdInstitutionType.juvenile_detention", "Facility for minors under custody. Managed by juvenile justice authorities."),
    MILITARY_BARRACKS     ("MBR", "householdInstitutionType.military_barracks", "Accommodation for soldiers in active service. Managed by military commanders."),
    REHABILITATION_CENTER ("RHC", "householdInstitutionType.rehabilitation_center", "Residential rehabilitation for substance abuse or social reintegration. Managed by specialized staff."),
    LONG_STAY_HOSPITAL    ("LSH", "householdInstitutionType.long_stay_hospital", "Healthcare facility for chronic, TB, or palliative patients. Managed by clinical staff."),
    HOMELESS_SHELTER      ("HMS", "householdInstitutionType.homeless_shelter", "Provide temporary shelter for homeless individuals. Managed by NGOs or social services."),
    WORK_CAMP             ("WKP", "householdInstitutionType.work_camp", "Residential camps for workers (construction, mining, agriculture). Managed by companies or contractors."),
    OTHER                 ("OTH", "householdInstitutionType.other", "Other type of institutional household not listed above.");

    String code
    String name   // i18n key
    String description

    HouseholdInstitutionType(String code, String name, String description){
        this.code = code
        this.name = name
        this.description = description
    }

    String getId(){ return code }

    @Override
    String toString() { return name }

    private static final Map<String, HouseholdInstitutionType> MAP = new HashMap<>()
    static {
        for (HouseholdInstitutionType e : values()) {
            MAP.put(e.code, e)
        }
    }

    static HouseholdInstitutionType getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
