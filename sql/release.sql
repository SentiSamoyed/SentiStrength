USE `sentisamoyed`;

DROP TABLE IF EXISTS `release`;

CREATE TABLE `release`
(
    `id`             BIGINT UNSIGNED NOT NULL COMMENT 'Release ID',
    `repo_full_name` VARCHAR(100)    NOT NULL UNIQUE COMMENT 'Repo full name',
    `tag_name`       VARCHAR(100)    NOT NULL UNIQUE COMMENT 'Release tag name',

    `created_at`     DATETIME        NOT NULL COMMENT 'Create time',
    `updated_at`     DATETIME        NOT NULL COMMENT 'Update time',
    CONSTRAINT PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARSET = `utf8mb4`
  COLLATE = `utf8mb4_general_ci`;

ALTER TABLE `release`
    ADD INDEX (`id`);

ALTER TABLE `release`
    ADD INDEX (`repo_full_name`, `tag_name`);

ALTER TABLE `release`
    ADD INDEX (`repo_full_name`, `created_at`);