-- Connect outside database first (as postgres user)
\connect postgres

DROP DATABASE IF EXISTS ensf380project;
CREATE DATABASE ensf380project;
\c ensf380project

CREATE TABLE Person (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    comments TEXT
);

CREATE TABLE Location (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address TEXT NOT NULL
);

CREATE TABLE DisasterVictim (
    person_id INTEGER PRIMARY KEY REFERENCES Person(id) ON DELETE CASCADE,
    date_of_birth DATE,
    approximate_age INTEGER,
    gender VARCHAR(50),
    entry_date DATE NOT NULL DEFAULT CURRENT_DATE,
    location_id INTEGER REFERENCES Location(id),
    is_soft_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE MedicalRecord (
    id SERIAL PRIMARY KEY,
    victim_id INTEGER NOT NULL REFERENCES DisasterVictim(person_id) ON DELETE CASCADE,
    treatment_details TEXT NOT NULL,
    treatment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    location_id INTEGER REFERENCES Location(id)
);

CREATE TABLE Supply (
    id SERIAL PRIMARY KEY,
    supply_type VARCHAR(100) NOT NULL,
    location_id INTEGER REFERENCES Location(id),
    victim_id INTEGER REFERENCES DisasterVictim(person_id),
    expiry_date DATE,
    allocation_date DATE,
    description TEXT
);

CREATE TABLE FamilyRelationship (
    id SERIAL PRIMARY KEY,
    person_one_id INTEGER NOT NULL REFERENCES Person(id) ON DELETE CASCADE,
    person_two_id INTEGER NOT NULL REFERENCES Person(id) ON DELETE CASCADE,
    relationship_type VARCHAR(50) NOT NULL
);

CREATE TABLE Inquiry (
    id SERIAL PRIMARY KEY,
    inquirer_id INTEGER NOT NULL REFERENCES Person(id),
    subject_person_id INTEGER REFERENCES Person(id),
    inquiry_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details TEXT NOT NULL
);

CREATE TABLE CulturalRequirement (
    id SERIAL PRIMARY KEY,
    victim_id INTEGER NOT NULL REFERENCES DisasterVictim(person_id) ON DELETE CASCADE,
    requirement_category VARCHAR(100) NOT NULL,
    requirement_option VARCHAR(100) NOT NULL
);

CREATE TABLE Skill (
    id SERIAL PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (category IN ('medical', 'language', 'trade')),
    CONSTRAINT unique_skill_name_category UNIQUE (skill_name, category)
);

CREATE TABLE VictimSkill (
    id SERIAL PRIMARY KEY,
    victim_id INTEGER NOT NULL REFERENCES DisasterVictim(person_id) ON DELETE CASCADE,
    skill_id INTEGER NOT NULL REFERENCES Skill(id) ON DELETE CASCADE,
    details VARCHAR(50),
    language_capabilities VARCHAR(100),
    certification_expiry DATE,
    proficiency_level VARCHAR(20) NOT NULL CHECK (proficiency_level IN ('beginner', 'intermediate', 'advanced'))
);

-- Grant permissions to oop user
ALTER DATABASE ensf380project OWNER TO oop;
GRANT ALL PRIVILEGES ON DATABASE ensf380project TO oop;
GRANT ALL ON ALL TABLES IN SCHEMA public TO oop;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO oop;

-- TESTING DATA FOR GRADING
-- Names indicate functionality for easy TA testing
-- All data is VALID and could be entered through the program

-- LOCATIONS
INSERT INTO Location (name, address) VALUES
('Test Location A', '123 Test St'),
('Test Location B', '456 Test Ave');

-- PEOPLE (functional names)
INSERT INTO Person (first_name, last_name, comments) VALUES
-- Age feature testing
('Approximate', 'AgePerson', 'Test approximate age only'),
('Birthdate', 'OnlyPerson', 'Test birthdate only'),
-- Deletion testing  
('SoftDelete', 'TestPerson', 'Will be soft deleted'),
('HardDelete', 'TestPerson', 'Will be hard deleted'),
-- Cultural requirements testing
('Cultural', 'RequirementPerson', 'Test cultural requirements'),
-- Skill testing
('Skill', 'TestPerson', 'Test all skill types'),
-- Supply testing
('Expired', 'SupplyPerson', 'Test expired supply'),
('Valid', 'SupplyPerson', 'Test valid supply');

-- DISASTER VICTIMS (all valid combinations)
INSERT INTO DisasterVictim (person_id, date_of_birth, approximate_age, gender, entry_date, location_id, is_soft_deleted) VALUES
-- Age feature: Approximate age only (valid)
((SELECT id FROM Person WHERE first_name = 'Approximate'), 
 NULL, 25, 'non-binary person', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location A'), FALSE),
-- Age feature: Birthdate only (valid)
((SELECT id FROM Person WHERE first_name = 'Birthdate'), 
 '2000-01-01', NULL, 'woman', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location A'), FALSE),
-- Deletion: Soft deleted (valid - soft delete is post-creation state)
((SELECT id FROM Person WHERE first_name = 'SoftDelete'), 
 '1995-05-15', NULL, 'man', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location A'), TRUE),
-- Deletion: Not deleted (valid - for hard delete test)
((SELECT id FROM Person WHERE first_name = 'HardDelete'), 
 NULL, 40, 'woman', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location B'), FALSE),
-- Cultural requirements (valid)
((SELECT id FROM Person WHERE first_name = 'Cultural'), 
 '1997-03-20', NULL, 'non-binary person', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location A'), FALSE),
-- Skills (valid)
((SELECT id FROM Person WHERE first_name = 'Skill'), 
 NULL, 32, 'man', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location B'), FALSE),
-- Supply testing: Person to test expired supply (valid)
((SELECT id FROM Person WHERE first_name = 'Expired'), 
 '1980-08-10', NULL, 'woman', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location A'), FALSE),
-- Supply testing: Person to test valid supply (valid)
((SELECT id FROM Person WHERE first_name = 'Valid'), 
 NULL, 50, 'man', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location B'), FALSE);

-- MEDICAL RECORDS (minimal, valid)
INSERT INTO MedicalRecord (victim_id, treatment_details, treatment_date, location_id) VALUES
((SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Approximate'),
 'Test treatment', '2025-01-01',
 (SELECT id FROM Location WHERE name = 'Test Location A'));

-- SUPPLIES (for expiry testing - all valid)
INSERT INTO Supply (supply_type, location_id, victim_id, expiry_date, allocation_date, description) VALUES
-- Expired perishable (valid - expiry in past)
('water', (SELECT id FROM Location WHERE name = 'Test Location A'), NULL, '2024-01-01', NULL, 'EXPIRED water'),
-- Non-expired perishable (valid)
('food', (SELECT id FROM Location WHERE name = 'Test Location A'), NULL, '2026-01-01', NULL, 'Valid food'),
-- Non-perishable (valid - no expiry)
('blanket', (SELECT id FROM Location WHERE name = 'Test Location B'), NULL, NULL, NULL, 'Test blanket'),
-- Allocated supply (valid)
('clothing', (SELECT id FROM Location WHERE name = 'Test Location B'), 
 (SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Valid'),
 NULL, '2025-01-01', 'Allocated clothing');

-- FAMILY RELATIONSHIPS (valid, one-way per IA1)
INSERT INTO FamilyRelationship (person_one_id, person_two_id, relationship_type) VALUES
((SELECT id FROM Person WHERE first_name = 'Approximate'),
 (SELECT id FROM Person WHERE first_name = 'Birthdate'),
 'sibling');

-- INQUIRY (valid)
INSERT INTO Inquiry (inquirer_id, subject_person_id, details) VALUES
((SELECT id FROM Person WHERE first_name = 'Birthdate'),
 (SELECT id FROM Person WHERE first_name = 'Approximate'),
 'Test inquiry about sibling');

-- CULTURAL REQUIREMENTS (using TEST file categories: AAA, BBB, CCC)
INSERT INTO CulturalRequirement (victim_id, requirement_category, requirement_option) VALUES
((SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Cultural'),
 'AAA', 'a1'),
((SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Cultural'),
 'BBB', 'x');

-- SKILLS (all categories, valid)
INSERT INTO Skill (skill_name, category) VALUES
-- Medical
('first-aid', 'medical'),
-- Language  
('TestLanguage', 'language'),
-- Trade
('carpentry', 'trade')
ON CONFLICT (skill_name, category) DO NOTHING;

-- VICTIM SKILLS (with all features, valid)
INSERT INTO VictimSkill (victim_id, skill_id, details, language_capabilities, certification_expiry, proficiency_level) VALUES
-- Medical skill with certification (valid)
((SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Skill'),
 (SELECT id FROM Skill WHERE skill_name = 'first-aid' AND category = 'medical'), 
 'Test Certification', NULL, '2026-01-01', 'intermediate'),
-- Language skill with capabilities (valid)
((SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Skill'),
 (SELECT id FROM Skill WHERE skill_name = 'TestLanguage' AND category = 'language'), 
 NULL, 'speak/listen', NULL, 'advanced'),
-- Trade skill (valid)
((SELECT person_id FROM DisasterVictim JOIN Person ON person_id = Person.id WHERE first_name = 'Skill'),
 (SELECT id FROM Skill WHERE skill_name = 'carpentry' AND category = 'trade'), 
 NULL, NULL, NULL, 'beginner');

-- (a) Spouse relationship between two existing people
INSERT INTO FamilyRelationship (person_one_id, person_two_id, relationship_type) VALUES
((SELECT id FROM Person WHERE first_name = 'Approximate'),
 (SELECT id FROM Person WHERE first_name = 'Birthdate'),
 'spouse');

-- (b) Parent relationship (one-way, per IA1 recommendation)
INSERT INTO FamilyRelationship (person_one_id, person_two_id, relationship_type) VALUES
((SELECT id FROM Person WHERE first_name = 'Cultural'),
 (SELECT id FROM Person WHERE first_name = 'Skill'),
 'parent');
