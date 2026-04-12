package edu.ucalgary.oop;

import org.junit.Test;

import static org.junit.Assert.*;

public class SkillTest {

     @Test
    public void testConstructorTradeSkillValid() {
        Skill skill = new Skill(3, "carpentry", "trade");

        assertEquals("Trade skill name should match", "carpentry", skill.getSkillName());
        assertEquals("Category should match", "trade", skill.getCategory());
    }

    @Test
    public void testConstructorMedicalSkillValid() {
        Skill skill = new Skill(1, "nursing", "medical");

        assertNotNull("Skill should be created", skill);
        assertEquals("ID should match", 1, skill.getId());
        assertEquals("Skill name should match", "nursing", skill.getSkillName());
        assertEquals("Category should match", "medical", skill.getCategory());
    }

    @Test
    public void testConstructorLanguageSkillValid() {
        Skill skill = new Skill(2, "Arabic", "language");

        assertEquals("Language skill name should match", "Arabic", skill.getSkillName());
        assertEquals("Category should match", "language", skill.getCategory());
    }
   

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidCategory() {
        new Skill(1, "nursing", "meow");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidMedicalSkill() {
        new Skill(1, "meow", "medical");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorInvalidTradeSkill() {
        new Skill(1, "meow", "trade");
    }

    @Test
    public void testSetCategoryNormalizesLowercase() {
        Skill skill = new Skill("Arabic", "language");
        skill.setCategory("MEDICAL");
        skill.setSkillName("doctor");

        assertEquals("Category should normalize to lowercase", "medical", skill.getCategory());
        assertEquals("Medical skill name should update", "doctor", skill.getSkillName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryBlankInvalid() {
        Skill skill = new Skill("Arabic", "language");
        skill.setCategory("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSkillNameBlankInvalid() {
        Skill skill = new Skill("Arabic", "language");
        skill.setSkillName("");
    }

    @Test
    public void testSetIdValid() {
        Skill skill = new Skill("Arabic", "language");
        skill.setId(5);

        assertEquals("ID should update", 5, skill.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdNegativeInvalid() {
        Skill skill = new Skill("Arabic", "language");
        skill.setId(-1);
    }
}