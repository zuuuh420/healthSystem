-- Apply once to existing databases created before display_name was added.
ALTER TABLE family_relation
    ADD COLUMN display_name VARCHAR(64) NULL AFTER relationship;
