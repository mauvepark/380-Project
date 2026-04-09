package edu.ucalgary.oop;

import java.time.LocalDate;
import java.util.*;

public class SkillService {
    private final SkillRepository repository;
    private final ActionLogger logger;
    private final VictimRepository victimRepository;

    // constructor
    public SkillService() {
        this.repository = new SkillRepository(DatabaseManager.getInstance().getConnection());
        this.victimRepository = new VictimRepository(DatabaseManager.getInstance().getConnection());
        this.logger = ActionLogger.getInstance();
    }

    // getters and setters
    public List<Skill> getAllSkills() {
        return repository.getAllSkills();
    }

    public List<Skill> getSkillsByCategory(String category) {
        validateCategory(category);
        return repository.getSkillsByCategory(category);
    }

    public List<VictimSkill> getSkillsForVictim(int victimId) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        return repository.getSkillsForVictim(victimId);
    }

    public List<Integer> searchVictimIdsBySkillCategory(String category) {
        validateCategory(category);
        return repository.searchVictimIdsBySkillCategory(category);
    }

    // add skill to victim
    public VictimSkill addSkillToVictim(int victimId, String skillName, String category, String details, 
                                        String languageCapabilities, LocalDate certificationExpiry, String proficiencyLevel) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        validateCategory(category);

        Skill skill = getOrCreateSkill(skillName, category);

        if (repository.victimHasSkill(victimId, skill.getId())) {
            throw new IllegalArgumentException("Victim already has this skill.");
        }

        validateVictimSkillData(skill, details, languageCapabilities, certificationExpiry, proficiencyLevel);

        int victimSkillId = repository.insertVictimSkill(victimId, skill.getId(), details, languageCapabilities, 
                                                         certificationExpiry, proficiencyLevel);

        VictimSkill victimSkill = new VictimSkill(victimSkillId, victimId, skill.getId(), details, languageCapabilities,
                                                  certificationExpiry, proficiencyLevel);

        logger.log("ADDED", "skill for victim " + victimId + " | Category: " + skill.getCategory() + " | Skill: " + skill.getSkillName());

        return victimSkill;
    }

    // delete a skill from a victim
    public void removeVictimSkill(int victimSkillId) {
        if (victimSkillId <= 0) {
            throw new IllegalArgumentException("Victim skill id must be positive.");
        }

        repository.deleteVictimSkill(victimSkillId);

        logger.log("DELETED", "victim skill " + victimSkillId);
    }

    // delete all skills for a victim for when a victim is hard deleted
    public void deleteAllSkillsForVictim(int victimId) {
        validateVictimId(victimId);
        validateVictimExistsAndIsActive(victimId);
        repository.deleteAllSkillsForVictim(victimId);
    }

    private Skill getOrCreateSkill(String skillName, String category) {
        Skill existingSkill = repository.getSkillByNameAndCategory(skillName, category);

        if (existingSkill != null) {
            return existingSkill;
        }

        int skillId = repository.insertSkill(skillName, category);
        return new Skill(skillId, skillName, category);
    }

    // validation
    private void validateVictimId(int victimId) {
        if (victimId <= 0) {
            throw new IllegalArgumentException("Victim id must be positive.");
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank.");
        }

        String normalized = category.trim().toLowerCase();

        if (!normalized.equals("medical")
                && !normalized.equals("language")
                && !normalized.equals("trade")) {
            throw new IllegalArgumentException("Category must be medical, language, or trade.");
        }
    }

    private void validateVictimSkillData(Skill skill, String details, String languageCapabilities,
                                         LocalDate certificationExpiry, String proficiencyLevel) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null.");
        }

        if (proficiencyLevel == null || proficiencyLevel.isBlank()) {
            throw new IllegalArgumentException("Proficiency level cannot be null or blank.");
        }

        String category = skill.getCategory();
        String skillName = skill.getSkillName();

        if (category.equals("medical")) {
            if (!skillName.equalsIgnoreCase("first-aid")
                    && !skillName.equalsIgnoreCase("counseling")
                    && !skillName.equalsIgnoreCase("nursing")
                    && !skillName.equalsIgnoreCase("doctor")) {
                throw new IllegalArgumentException("Invalid medical skill.");
            }

            if (certificationExpiry == null) {
                throw new IllegalArgumentException("Medical skills require certification expiry.");
            }

            if (details == null || details.isBlank()) {
                throw new IllegalArgumentException("Medical skills require certification details.");
            }

            if (languageCapabilities != null && !languageCapabilities.isBlank()) {
                throw new IllegalArgumentException("Medical skills cannot have language capabilities.");
            }
        }

        if (category.equals("language")) {
            if (languageCapabilities == null || languageCapabilities.isBlank()) {
                throw new IllegalArgumentException("Language skills require language capabilities.");
            }

            if (!isValidLanguageCapabilities(languageCapabilities)) {
                throw new IllegalArgumentException(
                    "Language capabilities must be: read/write, speak/listen, or both."
                );
            }

            if (certificationExpiry != null) {
                throw new IllegalArgumentException("Language skills cannot have certification expiry.");
            }

            if (details != null && details.isBlank()) {
                throw new IllegalArgumentException("Language skill details cannot be blank.");
            }
        }

        if (category.equals("trade")) {
            if (!skillName.equalsIgnoreCase("carpentry") && !skillName.equalsIgnoreCase("plumbing") && !skillName.equalsIgnoreCase("electricity")) {
                throw new IllegalArgumentException("Invalid trade skill.");
            }

            if (languageCapabilities != null && !languageCapabilities.isBlank()) {
                throw new IllegalArgumentException("Trade skills cannot have language capabilities.");
            }
        }
    }

    private boolean isValidLanguageCapabilities(String languageCapabilities) {
        String value = languageCapabilities.trim();
        if (value.equalsIgnoreCase("read/write")) {
            return true;
        }
        if (value.equalsIgnoreCase("speak/listen")) {
            return true;
        }
        if (value.equalsIgnoreCase("read/write, speak/listen")) {
            return true;
        }
        if (value.equalsIgnoreCase("speak/listen, read/write")) {
            return true;
        }

        return false;
    }

    private void validateVictimExistsAndIsActive(int victimId) {
        if (!victimRepository.isActiveVictim(victimId)) {
            throw new IllegalArgumentException("Victim does not exist or is soft deleted.");
        }
    }
}