USE `sentisamoyed`;

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment`
(
    `id`             BIGINT UNSIGNED NOT NULL COMMENT 'Comment ID',
    `repo_full_name` VARCHAR(100)    NOT NULL UNIQUE COMMENT 'Repo full name',
    `issue_number`   BIGINT UNSIGNED NOT NULL COMMENT 'Issue number',
    `html_url`       VARCHAR(100)    NOT NULL COMMENT 'Comment URL',

    `author`         VARCHAR(100)    NOT NULL COMMENT 'Username of the author',

    `created_at`     DATETIME        NOT NULL COMMENT 'Create time',
    `updated_at`     DATETIME        NOT NULL COMMENT 'Update time',

    `body`           TEXT            NOT NULL COMMENT 'Issue content',
    CONSTRAINT PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARSET = `utf8mb4`
  COLLATE = `utf8mb4_general_ci`;

ALTER TABLE `comment`
    ADD INDEX (`id`);

ALTER TABLE `comment`
    ADD INDEX (`repo_full_name`, `issue_number`, `id`);