package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class SkillServiceTest {

    private SkillService service;
    private MockSkillRepository mockRepository;
    private MockVictimRepository mockVictimRepository;
    private MockActionLogger mockLogger;

    @Before
    public void setUp() {
        mockRepository = new MockSkillRepository();
        mockVictimRepository = new MockVictimRepository();
        mockLogger = new MockActionLogger();

        service = new SkillService(mockRepository, mockLogger, mockVictimRepository);
    }

    @Test
    public void testAddMedicalSkillToVictimValid() {
        VictimSkill victimSkill = service.addSkillToVictim(
            1,
            "nursing",
            "medical",
            "license",
            null,
            LocalDate.parse("2026-01-01"),
            "advanced"
        );

        assertNotNull("Victim skill should be created", victimSkill);
        assertEquals("Victim ID should match", 1, victimSkill.getVictimId());
        assertEquals("Proficiency should match", "advanced", victimSkill.getProficiencyLevel());
    }

    @Test
    public void testAddLanguageSkillToVictimValid() {
        VictimSkill victimSkill = service.addSkillToVictim(
            1,
            "Arabic",
            "language",
            null,
            "read/write, speak/listen",
            null,
            "advanced"
        );

        assertNotNull("Language victim skill should be created", victimSkill);
        assertEquals("Language capabilities should match", "read/write, speak/listen", victimSkill.getLanguageCapabilities());
    }

    @Test
    public void testAddTradeSkillToVictimValid() {
        VictimSkill victimSkill = service.addSkillToVictim(
            1,
            "carpentry",
            "trade",
            null,
            null,
            null,
            "beginner"
        );

        assertNotNull("Trade victim skill should be created", victimSkill);
        assertEquals("Victim ID should match", 1, victimSkill.getVictimId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSkillToSoftDeletedVictimInvalid() {
        service.addSkillToVictim(
            999,
            "carpentry",
            "trade",
            null,
            null,
            null,
            "beginner"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalSkillWithoutDetailsInvalid() {
        service.addSkillToVictim(
            1,
            "doctor",
            "medical",
            null,
            null,
            LocalDate.parse("2026-01-01"),
            "advanced"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedicalSkillWithoutExpiryInvalid() {
        service.addSkillToVictim(
            1,
            "doctor",
            "medical",
            "license",
            null,
            null,
            "advanced"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLanguageSkillWithoutCapabilitiesInvalid() {
        service.addSkillToVictim(
            1,
            "Arabic",
            "language",
            null,
            null,
            null,
            "advanced"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTradeSkillWithLanguageInvalid() {
        service.addSkillToVictim(
            1,
            "plumbing",
            "trade",
            null,
            "read/write",
            null,
            "advanced"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateSkillInvalid() {
        service.addSkillToVictim(
            1,
            "carpentry",
            "trade",
            null,
            null,
            null,
            "advanced"
        );

        service.addSkillToVictim(
            1,
            "carpentry",
            "trade",
            null,
            null,
            null,
            "advanced"
        );
    }

    @Test
    public void testRemoveVictimSkillValid() {
        VictimSkill victimSkill = service.addSkillToVictim(
            1,
            "electricity",
            "trade",
            null,
            null,
            null,
            "advanced"
        );

        service.removeVictimSkill(victimSkill.getId());

        assertTrue("Victim skill list should be empty after removal", mockRepository.victimSkills.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveVictimSkillWithInvalidId() {
        service.removeVictimSkill(0);
    }

    private static class MockSkillRepository extends SkillRepository {
        private final Map<String, Skill> skills = new HashMap<>();
        private final List<VictimSkill> victimSkills = new ArrayList<>();
        private int nextSkillId = 1;
        private int nextVictimSkillId = 1;

        public MockSkillRepository() {
            super(null);
        }

        @Override
        public Skill getSkillByNameAndCategory(String skillName, String category) { 
            return skills.get(skillName.toLowerCase() + "|" + category.toLowerCase());
        }

        @Override
        public int insertSkill(String skillName, String category) {
            int id = nextSkillId++;
            Skill skill = new Skill(id, skillName, category);
            skills.put(skillName.toLowerCase() + "|" + category.toLowerCase(), skill);
            return id;
        }

        @Override
        public boolean victimHasSkill(int victimId, int skillId) {
            for (VictimSkill victimSkill : victimSkills) {
                if (victimSkill.getVictimId() == victimId && victimSkill.getSkillId() == skillId) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int insertVictimSkill(int victimId, int skillId, String details, String languageCapabilities,
                                     LocalDate certificationExpiry, String proficiencyLevel) {
            int id = nextVictimSkillId++;
            victimSkills.add(new VictimSkill(
                id,
                victimId,
                skillId,
                details,
                languageCapabilities,
                certificationExpiry,
                proficiencyLevel
            ));
            return id;
        }

        @Override
        public void deleteVictimSkill(int victimSkillId) {
            boolean removed = victimSkills.removeIf(v -> v.getId() == victimSkillId);
            if (!removed) {
                throw new RuntimeException("No victim skill found");
            }
        }
    }

    private static class MockVictimRepository extends VictimRepository {
        public MockVictimRepository() {
            super(null);
        }

        @Override
        public boolean isActiveVictim(int personId) {
            return personId == 1;
        }
    }

    private static class MockActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
        }
    }
}