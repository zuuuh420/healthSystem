-- Apply once after family_relation.sql.
CREATE TABLE IF NOT EXISTS family_device_snapshot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_user_id BIGINT NOT NULL,
  device_name VARCHAR(64) NOT NULL DEFAULT '知衡 Band 2',
  device_online TINYINT(1) NOT NULL DEFAULT 1,
  wearing TINYINT(1) NOT NULL DEFAULT 0,
  heart_rate INT NULL,
  oxygen INT NULL,
  temperature DECIMAL(3,1) NULL,
  sleep_minutes INT NULL,
  steps INT NULL,
  measured_at DATETIME NULL,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uk_family_snapshot_member UNIQUE (member_user_id),
  CONSTRAINT fk_family_snapshot_member FOREIGN KEY (member_user_id) REFERENCES user(id)
);

INSERT INTO family_device_snapshot
  (member_user_id, device_name, device_online, wearing, heart_rate, oxygen, temperature, sleep_minutes, steps, measured_at)
VALUES
  (2, '知衡 Band 2', 1, 1, 77, 99, 36.8, 438, 6842, NOW()),
  (3, '知衡 Band 2', 1, 0, NULL, NULL, NULL, NULL, NULL, NOW())
ON DUPLICATE KEY UPDATE
  device_name = VALUES(device_name);
