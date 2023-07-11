package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

import java.time.LocalDate
import java.time.Period

@Integration
@Transactional
class FakeEduReligDataGeneratorServiceSpec extends Specification {

    def fakeDataExecuted = 0

    void setupAll() {

    }

    void "Update Education Religion Database to Fake Data"() {
        println "\n#### Creating Fake Education and Religion Datasets ####"


        setupAll()

        initFakeData()


        expect:
        fakeDataExecuted == 1
    }

    def initFakeData() {
        // Generate mock data for education
        def educationValues = ['NO_EDU', 'EDU_01', 'EDU_02', 'EDU_03', 'EDU_04', 'EDU_05', 'OTHER']
        def educationPercentages = [30, 40, 20, 5, 3, 1, 1]
        def educationDistribution = calculateDistribution(educationPercentages)

        // Generate mock data for religion
        def religionValues = ['REL_01', 'REL_02', 'REL_03', 'REL_04', 'REL_05', 'REL_06', 'REL_07', 'REL_08', 'REL_09', 'NO_REL', 'OTHER', 'UNK']
        def religionPercentages = [26.2, 18.3, 15.1, 14.7, 1.6, 1.6, 1.6, 1.6, 5, 13.4, 1, 0.5]
        def religionDistribution = calculateDistribution(religionPercentages)

        // Update Member domain instances with mock data
        Member.list().each { member ->
            // Generate education based on age
            def educationIndex = getRandomIndex(educationDistribution)
            member.education = getEducationByAge(educationValues[educationIndex], member.dob)

            // Generate religion based on age
            def religionIndex = getRandomIndex(religionDistribution)
            member.religion = getReligionByAge(religionValues[religionIndex], member.dob)

            member.save(failOnError: true)
        }

        fakeDataExecuted=1
    }

    /**
     * Calculates the cumulative distribution for the given percentages.
     *
     * @param percentages The percentages for each value.
     * @return The cumulative distribution.
     */
    private List<Integer> calculateDistribution(List<Float> percentages) {
        def total = percentages.sum()
        def distribution = []
        def cumulative = 0

        percentages.each { percentage ->
            cumulative += percentage
            distribution.add(Math.round(cumulative * 100 / total))
        }

        return distribution
    }

    /**
     * Generates a random index based on the given distribution.
     *
     * @param distribution The cumulative distribution.
     * @return The random index.
     */
    private int getRandomIndex(List<Integer> distribution) {
        def random = new Random().nextInt(100)
        def index = 0

        while (index < distribution.size() && random >= distribution[index]) {
            index++
        }

        return index
    }

    /**
     * Retrieves the education level based on the member's age.
     *
     * @param education The default education level.
     * @param dob The member's date of birth.
     * @return The education level based on age.
     */
    private String getEducationByAge(String education, LocalDate dob) {
        def age = calculateAge(dob)

        // Adjust education level based on age
        if (age < 11) {
            // For members below 18 years old, assume primary education
            return 'EDU_01'
        } else {
            return education
        }
    }

    /**
     * Retrieves the religion based on the member's age.
     *
     * @param religion The default religion.
     * @param dob The member's date of birth.
     * @return The religion based on age.
     */
    private String getReligionByAge(String religion, LocalDate dob) {
        def age = calculateAge(dob)

        // Adjust religion based on age
        if (age < 0) {
            // For members below 18 years old, assume no religion
            return 'NO_REL'
        } else {
            return religion
        }
    }

    /**
     * Calculates the age based on the given date of birth.
     *
     * @param dob The date of birth.
     * @return The calculated age.
     */
    private int calculateAge(LocalDate dob) {
        def now = LocalDate.now()
        def age = Period.between(dob, now).getYears()

        return age
    }
}
