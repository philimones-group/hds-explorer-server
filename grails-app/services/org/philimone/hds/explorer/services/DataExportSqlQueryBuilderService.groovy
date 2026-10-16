package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.DataExportItem
import org.philimone.hds.explorer.server.model.main.Region

@Transactional(readOnly = true)
class DataExportSqlQueryBuilderService {

    String buildExportSql(ExportRequest request) {
        def ds = DataExportItem.getFrom(request.datasetName)

        if (ds == DataExportItem.RESIDENCY_PERSON_TIME) {
            return buildPersonTimeSql(request)
        } else if (ds == DataExportItem.MATERNAL_PREGNANCY) {
            return buildMaternalPregnancySql(request)
        } else if (ds == DataExportItem.HOUSEHOLD_COMPOSITION) {
            return buildHouseholdCompositionSql(request)
        } else if (ds == DataExportItem.INDEPTH_RESIDENCY_PERSON_TIME) {
            return buildIndepthCoreResidencySql(request)
        } else if (ds == DataExportItem.INDEPTH_MATERNAL_PREGNANCY) {
            return buildIndepthMaternalPregnancySql(request)
        } else if (ds == DataExportItem.INDEPTH_HOUSEHOLD_COMPOSITION) {
            return buildIndepthHouseholdCompositionSql(request)
        } else {
            return buildRegularTableSql(request)
        }
    }

    private String buildRegularTableSql(ExportRequest request) {
        String table = request.datasetName ? request.datasetName : "member"
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder()

        // 1. MEMBER & INDIVIDUAL COHORT Group (Group 1: Full Individual Cohort)
        if (table == "member" || table == "member_ext") {
            String fromClause = (table == "member") ? "FROM member m" : "FROM `${table}` t JOIN member m ON m.code = t.member_code"
            String selectCols = (table == "member") ? "SELECT m.*" : "SELECT t.*"

            sql = new StringBuilder("""
                ${selectCols}, 
                       r.household_id AS current_household_id, 
                       r.household_code AS current_household_code,
                       r.start_type AS current_residency_start_type,
                       r.start_date AS current_residency_start_date,
                       r.end_type AS current_residency_end_type,
                       r.end_date AS current_residency_end_date,
                       hr.relationship_type AS current_head_relationship_type,
                       TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS age_at_ref_date
                ${fromClause}
                LEFT JOIN residency r ON r.member_id = m.id 
                     AND r.start_date = (SELECT MAX(r2.start_date) FROM residency r2 WHERE r2.member_id = m.id AND (r2.status IS NULL OR r2.status <> 2))
                     AND (r.status IS NULL OR r.status <> 2)
                LEFT JOIN head_relationship hr ON hr.member_id = m.id 
                     AND hr.start_date = (SELECT MAX(hr2.start_date) FROM head_relationship hr2 WHERE hr2.member_id = m.id AND (hr2.status IS NULL OR hr2.status <> 2))
                     AND (hr.status IS NULL OR hr.status <> 2)
                LEFT JOIN household h ON h.id = r.household_id
                WHERE 1=1
            """)

            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND r.start_date <= ${refDateStr} AND (r.end_type = 'NA' OR r.end_date IS NULL OR r.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND (r.start_date IS NULL OR r.start_date <= ${refDateStr})")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (r.end_type = 'NA' OR r.end_date IS NULL)")
            }
            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 1B. RESIDENCY Table
        else if (table == "residency") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM residency t
                JOIN member m ON m.id = t.member_id
                LEFT JOIN household h ON h.id = t.household_id
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_type = 'NA' OR t.end_date IS NULL OR t.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND t.start_date <= ${refDateStr}")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_type = 'NA' OR t.end_date IS NULL)")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 1C. HEAD_RELATIONSHIP Table
        else if (table == "head_relationship") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM head_relationship t
                JOIN member m ON m.id = t.member_id
                LEFT JOIN household h ON h.id = t.household_id
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_type = 'NA' OR t.end_date IS NULL OR t.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND t.start_date <= ${refDateStr}")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_type = 'NA' OR t.end_date IS NULL)")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 1D. MARITAL_RELATIONSHIP Table
        else if (table == "marital_relationship") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM marital_relationship t
                LEFT JOIN member mA ON mA.id = t.member_a_id
                LEFT JOIN member mB ON mB.id = t.member_b_id
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND (mA.gender = '${request.gender}' OR mB.gender = '${request.gender}')")
            }
            if (request.ageMin != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) >= ${request.ageMin} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) >= ${request.ageMin})")
            }
            if (request.ageMax != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) <= ${request.ageMax} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) <= ${request.ageMax})")
            }
            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_status IS NULL OR t.end_status = 'NA' OR t.end_date IS NULL OR t.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND t.start_date <= ${refDateStr}")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_status IS NULL OR t.end_status = 'NA' OR t.end_date IS NULL)")
            }
            if (request.regionCode) {
                Region reg = Region.findByCode(request.regionCode.trim())
                if (reg) {
                    String colName = reg.hierarchyLevel ? reg.hierarchyLevel.code : "hierarchy1"
                    sql.append(""" AND EXISTS (
                        SELECT 1 FROM residency r 
                        JOIN household h ON h.id = r.household_id 
                        WHERE (r.member_id = mA.id OR r.member_id = mB.id) 
                          AND r.start_date = (SELECT MAX(r2.start_date) FROM residency r2 WHERE r2.member_id = r.member_id AND (r2.status IS NULL OR r2.status <> 2))
                          AND (r.status IS NULL OR r.status <> 2)
                          AND h.${colName} = '${reg.code}'
                    )""")
                }
            }
        }
        // 1E. MARITAL_RELATIONSHIP_EXT Form
        else if (table == "marital_relationship_ext") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM marital_relationship_ext t
                LEFT JOIN member mA ON mA.code = t.member_a
                LEFT JOIN member mB ON mB.code = t.member_b
                LEFT JOIN visit v ON v.code = t.visit_code
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND (mA.gender = '${request.gender}' OR mB.gender = '${request.gender}')")
            }
            if (request.ageMin != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) >= ${request.ageMin} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) >= ${request.ageMin})")
            }
            if (request.ageMax != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) <= ${request.ageMax} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) <= ${request.ageMax})")
            }
            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 2. HOUSEHOLD STRUCTURAL Group (Group 2: Household Unit & Extensions)
        else if (table == "household" || table.startsWith("household_") || table == "change_head_ext") {
            String hhJoinCol = (table.startsWith("household_relocation")) ? "origin_code" : "household_code"
            String fromClause = (table == "household") ? "FROM household h" : "FROM `${table}` t JOIN household h ON h.code = t.${hhJoinCol}"
            String selectCols = (table == "household") ? "SELECT h.*" : "SELECT t.*"

            sql = new StringBuilder("""
                ${selectCols}
                ${fromClause}
                WHERE 1=1
            """)

            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 3. VISIT & FIELD ENCOUNTER Group (Group 3: Encounters & Extensions)
        else if (table == "visit" || table.startsWith("visit_") || table.startsWith("incomplete_visit")) {
            String fromClause = (table == "visit") ? "FROM visit v" : "FROM `${table}` t JOIN visit v ON v.code = t.visit_code"
            String selectCols = (table == "visit") ? "SELECT v.*" : "SELECT t.*"

            sql = new StringBuilder("""
                ${selectCols}
                ${fromClause}
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 4. HISTORICAL EVENT LOGS Group (Group 4: Events - Death, Migrations, Enumeration)
        else if (table.startsWith("death") || table.contains("migration") || table == "enumeration") {
            boolean isExt = table.endsWith("_ext")
            String dateCol = isExt ? "v.visit_date" : "t." + ((table.startsWith("death")) ? "death_date" : ((table.contains("migration")) ? "migration_date" : "event_date"))
            String memberJoin = isExt ? "JOIN member m ON m.code = t.member_code" : "JOIN member m ON m.id = t.member_id"
            String visitJoin = isExt ? "LEFT JOIN visit v ON v.code = t.visit_code" : "LEFT JOIN visit v ON v.id = t.visit_id"
            String hhJoin = (table == "enumeration") ? "LEFT JOIN household h ON h.id = t.household_id" : "LEFT JOIN household h ON h.code = v.household_code"

            sql = new StringBuilder("""
                SELECT t.*
                FROM `${table}` t
                ${memberJoin}
                ${visitJoin}
                ${hhJoin}
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND ${dateCol} <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 5. REPRODUCTIVE & MATERNAL SURVEILLANCE Group (Group 5: Pregnancy & Extensions)
        else if (table == "pregnancy_child") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_child` t
                JOIN pregnancy_outcome po ON po.id = t.pregnancy_outcome_id
                JOIN member mother ON mother.id = po.mother_id
                LEFT JOIN visit v ON v.id = po.visit_id
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND po.outcome_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table == "pregnancy_child_ext") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_child_ext` t
                JOIN pregnancy_outcome_ext poe ON poe.id = t.pregnancy_outcome_ext_id
                JOIN member mother ON mother.code = poe.mother_code
                LEFT JOIN visit v ON v.code = poe.visit_code
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table == "pregnancy_visit_child") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_visit_child` t
                JOIN pregnancy_visit pv ON pv.id = t.pregnancy_visit_id
                JOIN member mother ON mother.id = pv.mother_id
                LEFT JOIN visit v ON v.id = pv.visit_id
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND pv.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table == "pregnancy_visit_child_ext") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_visit_child_ext` t
                JOIN pregnancy_visit_ext pve ON pve.id = t.pregnancy_visit_ext_id
                JOIN member mother ON mother.code = pve.mother_code
                LEFT JOIN visit v ON v.code = pve.visit_code
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table.startsWith("pregnancy_")) {
            boolean isExt = table.endsWith("_ext")
            String motherJoin = isExt ? "JOIN member mother ON mother.code = t.mother_code" : "JOIN member mother ON mother.id = t.mother_id"
            String visitJoin = isExt ? "LEFT JOIN visit v ON v.code = t.visit_code" : "LEFT JOIN visit v ON v.id = t.visit_id"
            String dateCol = isExt ? "v.visit_date" : ((table.startsWith("pregnancy_outcome")) ? "t.outcome_date" : ((table.startsWith("pregnancy_visit")) ? "t.visit_date" : "t.recorded_date"))

            sql = new StringBuilder("""
                SELECT t.*
                FROM `${table}` t
                ${motherJoin}
                ${visitJoin}
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null && dateCol) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null && dateCol) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate && dateCol) {
                sql.append(" AND ${dateCol} <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 6. REGION & ADMINISTRATIVE MASTER Group
        else if (table == "region" || table == "region_ext") {
            String colName = (table == "region") ? "code" : "region_code"
            if (request.regionCode) {
                sql = new StringBuilder(buildRegionCteSql(table, request.regionCode, colName))
            } else {
                sql = new StringBuilder("SELECT t.* FROM `${table}` t WHERE 1=1")
            }
        }
        else if (table == "region_head_relationship" || table == "change_region_head_ext") {
            if (request.regionCode) {
                sql = new StringBuilder(buildRegionCteSql(table, request.regionCode, "region_code"))
            } else {
                sql = new StringBuilder("SELECT t.* FROM `${table}` t WHERE 1=1")
            }
        }
        else if (table == "round") {
            sql = new StringBuilder("SELECT t.* FROM `round` t WHERE 1=1")
            if (request.referenceDate) {
                sql.append(" AND t.start_date <= ${refDateStr}")
            }
        }
        // 7. GENERIC FALLBACK FOR ALL OTHER SYSTEM TABLES (_user, etc.)
        else {
            sql = new StringBuilder("SELECT t.* FROM `${table}` t WHERE 1=1")
        }

        // Global Native SQL Random Sub-Sampling
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    String buildChildTableSql(String childTableName, String immediateParentTableName, ExportRequest request) {
        List<String> parentIds = request.exportedTableIdsMap.get(immediateParentTableName)
        if (!parentIds || parentIds.isEmpty()) {
            return "SELECT c.* FROM `${childTableName}` c WHERE 1=0"
        }

        String fkCol = "${immediateParentTableName}_id"

        // Chunk parent BIGINT IDs into groups of 1,000 for SQL safety
        List<List<String>> chunks = parentIds.collate(1000)
        String whereCondition = chunks.collect { chunk ->
            String idList = chunk.collect { it.replaceAll("[^0-9]", "") }.findAll { !it.empty }.join(",")
            return "c.${fkCol} IN (${idList})"
        }.join(" OR ")

        return "SELECT c.* FROM `${childTableName}` c WHERE ${whereCondition}"
    }

    private String buildIndepthCoreResidencySql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder filterWhere = new StringBuilder(" WHERE (r.status IS NULL OR r.status <> 2)")
        String filterRand = ""

        if (request.referenceDate) {
            filterWhere.append(" AND r.start_date <= ${refDateStr} AND (r.end_date IS NULL OR r.end_date >= ${refDateStr})")
        }
        if (request.gender && request.gender != "ALL") {
            filterWhere.append(" AND m.gender = '${request.gender}'")
        }
        if (request.ageMin != null) {
            filterWhere.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            filterWhere.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.regionCode) {
            filterWhere.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            filterRand = " WHERE RAND() <= (${request.randomSamplePercent} / 100.0)"
        }

        String sql = """
            SELECT * FROM (
                SELECT m.code AS IndividualId,
                       r.household_code AS LocationId,
                       m.dob AS DoB,
                       m.gender AS Sex,
                       CASE r.start_type
                           WHEN 'ENU' THEN 'ENU'
                           WHEN 'BIR' THEN 'BTH'
                           WHEN 'XEN' THEN 'IMG'
                           WHEN 'ENT' THEN 'ENT'
                           ELSE r.start_type
                       END AS EventCode,
                       r.start_date AS EventDate,
                       1 AS residence,
                       TIMESTAMPDIFF(YEAR, m.dob, r.start_date) AS age_at_event,
                       reg.name AS region_name,
                       reg.code AS region_code
                FROM residency r
                JOIN member m ON m.id = r.member_id
                JOIN household h ON h.id = r.household_id
                LEFT JOIN region reg ON reg.code = h.region
                ${filterWhere.toString()}

                UNION ALL

                SELECT m.code AS IndividualId,
                       r.household_code AS LocationId,
                       m.dob AS DoB,
                       m.gender AS Sex,
                       CASE 
                           WHEN r.end_type = 'CHG' THEN 'EXT'
                           WHEN r.end_type = 'EXT' THEN 'OMG'
                           WHEN r.end_type = 'DTH' THEN 'DTH'
                           WHEN r.end_type = 'NA' OR r.end_date IS NULL THEN 'OBE'
                           ELSE COALESCE(r.end_type, 'OBE')
                       END AS EventCode,
                       COALESCE(r.end_date, ${refDateStr}) AS EventDate,
                       IF(r.end_type = 'NA' OR r.end_date IS NULL, 1, 0) AS residence,
                       TIMESTAMPDIFF(YEAR, m.dob, COALESCE(r.end_date, ${refDateStr})) AS age_at_event,
                       reg.name AS region_name,
                       reg.code AS region_code
                FROM residency r
                JOIN member m ON m.id = r.member_id
                JOIN household h ON h.id = r.household_id
                LEFT JOIN region reg ON reg.code = h.region
                ${filterWhere.toString()}
            ) eha
            ${filterRand}
            ORDER BY eha.IndividualId, eha.EventDate ASC
        """

        return sql
    }

    private String buildPersonTimeSql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT m.code AS member_code, 
                   m.name AS member_name, 
                   m.gender AS member_gender, 
                   m.dob AS member_dob,
                   TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS member_age_at_ref_date,
                   r.household_code, 
                   h.name AS household_name,
                   r.start_type, 
                   r.start_date, 
                   COALESCE(r.end_type, 'OBE') AS end_type, 
                   COALESCE(r.end_date, ${refDateStr}) AS end_date,
                   DATEDIFF(COALESCE(r.end_date, ${refDateStr}), r.start_date) AS exposure_days,
                   ROUND(DATEDIFF(COALESCE(r.end_date, ${refDateStr}), r.start_date) / 365.25, 3) AS person_years,
                   reg.code AS region_code,
                   reg.name AS region_name
            FROM residency r
            JOIN member m ON m.id = r.member_id
            JOIN household h ON h.id = r.household_id
            LEFT JOIN region reg ON reg.code = h.region
            WHERE (r.status IS NULL OR r.status <> 2)
        """)

        if (request.referenceDate) {
            sql.append(" AND r.start_date <= ${refDateStr} AND (r.end_date IS NULL OR r.end_date >= ${refDateStr})")
        }
        if (request.gender && request.gender != "ALL") {
            sql.append(" AND m.gender = '${request.gender}'")
        }
        if (request.ageMin != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildIndepthMaternalPregnancySql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT m.code AS IndividualId,
                   f.code AS FatherId,
                   child.code AS ChildId,
                   'DLV' AS EventCode,
                   po.outcome_date AS EventDate,
                   v.visit_date AS ObservationDate,
                   child.gender AS ChildSex,
                   po.number_of_outcomes AS MultiBirth,
                   po.number_of_livebirths AS LiveBirths,
                   (po.number_of_outcomes - po.number_of_livebirths) AS StillBirths,
                   h.code AS LocationId,
                   m.name AS mother_name,
                   f.name AS father_name,
                   child.name AS child_name,
                   pc.outcome_type AS child_outcome_type,
                   TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) AS mother_age_at_outcome,
                   reg.name AS region_name
            FROM pregnancy_outcome po
            JOIN member m ON m.id = po.mother_id
            LEFT JOIN member f ON f.id = po.father_id
            JOIN pregnancy_child pc ON pc.pregnancy_outcome_id = po.id
            LEFT JOIN member child ON child.id = pc.child_id
            LEFT JOIN visit v ON v.id = po.visit_id
            LEFT JOIN household h ON h.code = v.household_code
            LEFT JOIN region reg ON reg.code = h.region
            WHERE 1=1
        """)

        if (request.ageMin != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.referenceDate) {
            sql.append(" AND po.outcome_date <= ${refDateStr}")
        }
        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildMaternalPregnancySql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT po.code AS pregnancy_code, 
                   po.outcome_date,                   
                   m.code AS mother_code, 
                   m.name AS mother_name, 
                   TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) AS mother_age_at_outcome,
                   f.code AS father_code, 
                   f.name AS father_name,
                   po.number_of_outcomes, 
                   po.number_of_livebirths,
                   pc.outcome_type AS child_outcome_type, 
                   child.code AS child_code, 
                   child.name AS child_name, 
                   child.gender AS child_gender,
                   h.code AS household_code,
                   h.region AS region_code
            FROM pregnancy_outcome po
            JOIN member m ON m.id = po.mother_id
            LEFT JOIN member f ON f.id = po.father_id
            JOIN pregnancy_child pc ON pc.pregnancy_outcome_id = po.id
            LEFT JOIN member child ON child.id = pc.child_id
            LEFT JOIN visit v ON v.id = po.visit_id
            LEFT JOIN household h ON h.code = v.household_code
            WHERE 1=1
        """)

        if (request.ageMin != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.referenceDate) {
            sql.append(" AND po.outcome_date <= ${refDateStr}")
        }
        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildIndepthHouseholdCompositionSql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT h.code AS LocationId,
                   m.code AS IndividualId,
                   m.dob AS DoB,
                   m.gender AS Sex,
                   head.code AS HeadId,
                   head.gender AS HeadSex,
                   hr.relationship_type AS RelationshipToHead,
                   h.name AS household_name,
                   h.type AS household_type,
                   h.institution_type AS household_institution_type,
                   m.name AS member_name,
                   head.name AS head_name,
                   TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS age_at_ref_date,
                   r.start_date AS resident_since,
                   ph.proxy_head_name AS ProxyHeadName,
                   ph.proxy_head_role AS ProxyHeadRole,
                   reg.code AS region_code,
                   reg.name AS region_name
            FROM household h
            LEFT JOIN member head ON head.id = h.head_id
            LEFT JOIN household_proxy_head ph ON ph.id = h.proxy_head_id
            JOIN residency r ON r.household_id = h.id 
                 AND r.start_date <= ${refDateStr} 
                 AND (r.end_date IS NULL OR r.end_date >= ${refDateStr}) 
                 AND (r.status IS NULL OR r.status <> 2)
            JOIN member m ON m.id = r.member_id
            LEFT JOIN head_relationship hr ON hr.member_id = m.id AND hr.household_id = h.id 
                 AND hr.start_date = (SELECT MAX(hr2.start_date) FROM head_relationship hr2 WHERE hr2.member_id = m.id AND hr2.household_id = h.id AND (hr2.status IS NULL OR hr2.status <> 2))
            LEFT JOIN region reg ON reg.code = h.region
            WHERE 1=1
        """)

        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        sql.append(" ORDER BY h.code, m.code")

        return sql.toString()
    }

    private String buildHouseholdCompositionSql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT h.code AS household_code,
                   h.name AS household_name,
                   h.type AS household_type,
                   h.institution_type AS household_institution_type,
                   head.code AS head_code,
                   head.name AS head_name,
                   head.gender AS head_gender,
                   m.code AS member_code,
                   m.name AS member_name,
                   m.gender AS member_gender,
                   m.dob AS member_dob,
                   TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS member_age_at_ref_date,
                   hr.relationship_type,
                   r.start_date AS resident_since,
                   ph.proxy_head_name,
                   ph.proxy_head_role,
                   reg.code AS region_code,
                   reg.name AS region_name
            FROM household h
            LEFT JOIN member head ON head.id = h.head_id
            LEFT JOIN household_proxy_head ph ON ph.id = h.proxy_head_id
            JOIN residency r ON r.household_id = h.id 
                 AND r.start_date <= ${refDateStr} 
                 AND (r.end_date IS NULL OR r.end_date >= ${refDateStr}) 
                 AND (r.status IS NULL OR r.status <> 2)
            JOIN member m ON m.id = r.member_id
            LEFT JOIN head_relationship hr ON hr.member_id = m.id AND hr.household_id = h.id 
                 AND hr.start_date = (SELECT MAX(hr2.start_date) FROM head_relationship hr2 WHERE hr2.member_id = m.id AND hr2.household_id = h.id AND (hr2.status IS NULL OR hr2.status <> 2))
            LEFT JOIN region reg ON reg.code = h.region
            WHERE 1=1
        """)

        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        sql.append(" ORDER BY h.code, m.code")

        return sql.toString()
    }

    String buildRegionFilterClause(String regionCode, String householdAlias = "h") {
        if (!regionCode || regionCode.trim().isEmpty()) return ""

        Region reg = Region.findByCode(regionCode.trim())
        if (!reg) return ""

        String colName = reg.hierarchyLevel ? reg.hierarchyLevel.code : "hierarchy1"
        return " AND ${householdAlias}.${colName} = '${reg.code}'"
    }

    private static String buildRegionCteSql(String tableName, String regionCode, String regionColumnName = "region_code") {
        if (!regionCode || regionCode.trim().isEmpty()) {
            return "SELECT t.* FROM `${tableName}` t WHERE 1=1"
        }

        String code = regionCode.trim()
        return """
            WITH RECURSIVE region_tree AS (
                SELECT code FROM region WHERE code = '${code}'
                UNION ALL
                SELECT r.code FROM region r INNER JOIN region_tree rt ON r.parent_region_code = rt.code
            )
            SELECT t.* 
            FROM `${tableName}` t
            WHERE 1=1 AND t.${regionColumnName} IN (SELECT code FROM region_tree)
        """
    }

}
