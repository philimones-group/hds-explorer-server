package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.DateAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.GpsAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.NameAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.PhoneAnonymizationMode
import org.philimone.hds.forms.model.enums.SensitiveType

import java.time.LocalDate

@Transactional(readOnly = true)
class DataExportAnonymizationService {

    // --- PII Masking Constants ---
    private static final String PHONE_MASK_TEMPLATE = "***-***-****"
    private static final String NAME_MASK_SUFFIX = "***"
    private static final int DATE_SHIFT_OFFSET_DAYS = 7

    AnonymizationResult anonymizeValue(ExportRequest request, DataDictionaryService.ColumnMetadata colMeta, Object value) {
        SensitiveType sensType = colMeta?.sensitiveType ?: SensitiveType.NOT_APPLICABLE
        def skipColumnResult = new AnonymizationResult(value, true)

        // 1. Person Names
        if (sensType == SensitiveType.PERSON_NAME) {
            if (request.nameAnonymizationMode == NameAnonymizationMode.REMOVE) {
                return skipColumnResult // Skip column
            } else if (request.nameAnonymizationMode == NameAnonymizationMode.INITIALS) {
                value = maskName(value.toString())
            }
        }
        // 2. Phone Numbers
        else if (sensType == SensitiveType.PHONE_NUMBER) {
            if (request.phoneAnonymizationMode == PhoneAnonymizationMode.REMOVE) {
                return skipColumnResult // Skip column
            } else if (request.phoneAnonymizationMode == PhoneAnonymizationMode.MASK) {
                value = PHONE_MASK_TEMPLATE
            }
        }
        // 3. GPS Coordinates
        else if (sensType == SensitiveType.GPS) {
            if (request.gpsAnonymizationMode == GpsAnonymizationMode.REMOVE) {
                return skipColumnResult // Skip column
            } else if (request.gpsAnonymizationMode == GpsAnonymizationMode.ROUNDED) {
                if (value instanceof Double) {
                    value = Math.round(((Double) value).doubleValue() * 100.0) / 100.0
                }
            }
        }
        // 4. Dates
        else if (sensType == SensitiveType.DATE) {

            if (request.dateAnonymizationMode == DateAnonymizationMode.REMOVE) {
                return skipColumnResult // Skip column
            } else if (request.dateAnonymizationMode == DateAnonymizationMode.SHIFT_DATES && value != null) {
                if (value instanceof LocalDate) {
                    value = ((LocalDate) value).minusDays(DATE_SHIFT_OFFSET_DAYS)
                } else {
                    try {
                        String s = value.toString()
                        if (s.length() >= 10) {
                            LocalDate parsed = LocalDate.parse(s.substring(0, 10))
                            value = parsed.minusDays(DATE_SHIFT_OFFSET_DAYS).toString()
                        }
                    } catch (Exception e) {
                        // Keep original if unparseable
                    }
                }
            }
        }

        return new AnonymizationResult(value, false)
    }

    private static String maskName(String name) {
        if (!name) return ""
        String[] parts = name.split("\\s+")
        return parts.collect { p -> p.length() > 1 ? p.substring(0, 1) + NAME_MASK_SUFFIX : p }.join(" ")
    }

    class AnonymizationResult {
        Object value
        Boolean skipColumn = false

        AnonymizationResult() {
        }

        AnonymizationResult(Object value, Boolean skipColumn) {
            this.value = value
            this.skipColumn = skipColumn
        }
    }
}
