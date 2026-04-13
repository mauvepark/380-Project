package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for the CulturalRequirementService class.
 * These tests check adding, updating, deleting, and retrieving cultural requirements.
 */
public class CulturalRequirementServiceTest {

    private CulturalRequirementService service;
    private MockCulturalRequirementRepository mockRepository;
    private MockActionLogger mockLogger;
    private Map<String, Set<String>> options;

    @Before
    public void setUp() {
        mockRepository = new MockCulturalRequirementRepository();
        mockLogger = new MockActionLogger();

        options = new HashMap<>();
        options.put("dietary restrictions", new HashSet<>(Arrays.asList("halal", "vegetarian")));
        options.put("safe-space requirements", new HashSet<>(Arrays.asList("LGBTQIA+ affirming")));

        service = new CulturalRequirementService(mockRepository, options, mockLogger);
    }

    @Test
    public void testGetAvailableOptions() {
        Map<String, Set<String>> returnedOptions = service.getAvailableOptions();

        assertNotNull("Available options should not be null", returnedOptions);
        assertTrue("Options should contain dietary restrictions category", returnedOptions.containsKey("dietary restrictions"));
        assertTrue("Options should contain halal", returnedOptions.get("dietary restrictions").contains("halal"));
    }

    @Test
    public void testAddRequirementValid() {
        service.addRequirement(1, "dietary restrictions", "halal");

        List<CulturalRequirement> requirements = service.getRequirementsForVictim(1);

        assertEquals("Victim should have one requirement after adding requirement", 1, requirements.size());
        assertEquals("Requirement category should match", "dietary restrictions", requirements.get(0).getCategory());
        assertEquals("Requirement option should match", "halal", requirements.get(0).getOption());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRequirementDuplicateCategory() {
        service.addRequirement(1, "dietary restrictions", "halal");
        service.addRequirement(1, "dietary restrictions", "vegetarian");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRequirementInvalidCategory() {
        service.addRequirement(1, "invalid category", "halal");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddRequirementInvalidOption() {
        service.addRequirement(1, "dietary restrictions", "kosher");
    }

    @Test
    public void testRemoveRequirement() {
        service.addRequirement(1, "dietary restrictions", "halal");
        service.removeRequirement(1, "dietary restrictions");

        List<CulturalRequirement> requirements = service.getRequirementsForVictim(1);

        assertTrue("Victim should have no requirements after removal", requirements.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveRequirementThatDoesNotExist() {
        service.removeRequirement(1, "dietary restrictions");
    }

    @Test
    public void testGetCategories() {
        List<String> categories = service.getSortedCategories();

        assertEquals("Should return two categories", 2, categories.size());
        assertEquals("First category should be dietary restrictions", "dietary restrictions", categories.get(0));
    }

    @Test
    public void testGetOptionsForCategory() {
        List<String> sortedOptions = service.getSortedOptionsForCategory("dietary restrictions");

        assertEquals("Should return two options", 2, sortedOptions.size());
        assertEquals("First sorted option should be halal", "halal", sortedOptions.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOptionsForCategoryInvalid() {
        service.getSortedOptionsForCategory("invalid");
    }

    private static class MockCulturalRequirementRepository extends CulturalRequirementRepository {
        private final Map<Integer, List<CulturalRequirement>> data = new HashMap<>();

        public MockCulturalRequirementRepository() {
            super(null);
        }

        @Override
        public void insertRequirement(int victimId, String category, String option) {
            data.computeIfAbsent(victimId, k -> new ArrayList<>()).add(new CulturalRequirement(victimId, category, option));
        }

        @Override
        public void updateRequirement(int victimId, String category, String newOption) {
            List<CulturalRequirement> requirements = data.getOrDefault(victimId, new ArrayList<>());

            for (CulturalRequirement requirement : requirements) {
                if (requirement.getCategory().equals(category)) {
                    requirement.setOption(newOption);
                    return;
                }
            }

            throw new RuntimeException("No requirement found");
        }

        @Override
        public void deleteRequirement(int victimId, String category) {
            List<CulturalRequirement> requirements = data.getOrDefault(victimId, new ArrayList<>());
            boolean removed = requirements.removeIf(r -> r.getCategory().equals(category));

            if (!removed) {
                throw new RuntimeException("No requirement found");
            }
        }

        @Override
        public List<CulturalRequirement> getRequirementsForVictim(int victimId) {
            return new ArrayList<>(data.getOrDefault(victimId, new ArrayList<>()));
        }

        @Override
        public boolean victimHasRequirement(int victimId, String category) {
            List<CulturalRequirement> requirements = data.getOrDefault(victimId, new ArrayList<>());

            for (CulturalRequirement requirement : requirements) {
                if (requirement.getCategory().equals(category)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class MockActionLogger extends ActionLogger {
        @Override
        public void log(String actionType, String description) {
        }
    }
}
