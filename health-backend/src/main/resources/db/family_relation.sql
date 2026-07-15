-- Apply once to the existing health_system database.
-- Existing installations should run the ALTER statement only once.
ALTER TABLE user
    ADD COLUMN invite_code VARCHAR(24) NULL UNIQUE;

UPDATE user
SET invite_code = CONCAT('ZH', UPPER(SUBSTRING(REPLACE(UUID(), '-', ''), 1, 10)))
WHERE invite_code IS NULL;

ALTER TABLE user
    MODIFY COLUMN invite_code VARCHAR(24) NOT NULL UNIQUE;

CREATE TABLE IF NOT EXISTS family_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_user_id BIGINT NOT NULL,
  member_user_id BIGINT NOT NULL,
  relationship VARCHAR(32) NOT NULL,
  display_name VARCHAR(64) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uk_family_owner_member UNIQUE (owner_user_id, member_user_id),
  CONSTRAINT fk_family_owner FOREIGN KEY (owner_user_id) REFERENCES user(id),
  CONSTRAINT fk_family_member FOREIGN KEY (member_user_id) REFERENCES user(id)
);
