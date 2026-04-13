package edu.ucalgary.oop;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;
import java.util.Set;

/**
 * Unit tests for the CulturalRequirementLoader class.
 * These tests check that cultural requirement options are loaded correctly.
 */
public class CulturalRequirementLoaderTest {

    @Test
    public void testConstructorLoadsOptionsValid() {
        CulturalRequirementLoader loader = new CulturalRequirementLoader();

        assertNotNull("Loader should be created successfully", loader);
        assertNotNull("Loader should load a non-null accommodations map", loader.getAccomodations());
        assertFalse("Loaded accommodations map should not be empty", loader.getAccomodations().isEmpty());
    }

    @Test
    public void testGetAccomodationsReturnsExpectedCategories() {
        CulturalRequirementLoader loader = new CulturalRequirementLoader();
        Map<String, Set<String>> accommodations = loader.getAccomodations();

        assertNotNull("Accommodations map should not be null", accommodations);
        assertTrue("Accommodations map should contain at least one category", accommodations.size() > 0);
    }

    @Test
    public void testReturnedMapIsUnmodifiable() {
        CulturalRequirementLoader loader = new CulturalRequirementLoader();
        Map<String, Set<String>> accommodations = loader.getAccomodations();

        boolean throwException = false;

        try {
            accommodations.put("new category", Set.of("new option"));
        } catch (UnsupportedOperationException e) {
            throwException = true;
        }

        assertTrue("Returned accommodations map should be unmodifiable", throwException);
    }

    @Test
    public void testCategoriesNonNullOptions() {
        CulturalRequirementLoader loader = new CulturalRequirementLoader();
        Map<String, Set<String>> accommodations = loader.getAccomodations();

        boolean allNotNull = true;

        for (String category : accommodations.keySet()) {
            if (accommodations.get(category) == null) {
                allNotNull = false;
            }
        }

        assertTrue("Each category should have a non-null option set",allNotNull);
    }

    @Test
    public void testCategoriesAreNotBlank() {
        CulturalRequirementLoader loader = new CulturalRequirementLoader();
        Map<String, Set<String>> accommodations = loader.getAccomodations();

        boolean allNonBlank = true;

        for (String category : accommodations.keySet()) {
            if (category == null || category.isBlank()) {
                allNonBlank = false;
            }
        }

        assertTrue("Each category name should be not null and not blank",allNonBlank);
    }
}
