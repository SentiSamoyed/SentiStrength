USE `sentisamoyed`;

# DROP TABLE IF EXISTS `issue`;
CREATE TABLE `issue`
(
    `id`             BIGINT UNSIGNED NOT NULL COMMENT 'Issue ID',
    `repo_full_name` VARCHAR(100)    NOT NULL COMMENT 'Repo full name',
    `issue_number`   BIGINT UNSIGNED NOT NULL COMMENT 'Issue number',
    `title`          TEXT            NULL COMMENT 'Title',
    `state`          VARCHAR(10)     NOT NULL COMMENT 'Issue status (open, close)',
    `html_url`       VARCHAR(100)    NOT NULL COMMENT 'Issue URL',

    `author`         VARCHAR(100)    NOT NULL COMMENT 'Username of the author',

    `created_at`     DATETIME        NOT NULL COMMENT 'Create time',
    `updated_at`     DATETIME        NOT NULL COMMENT 'Update time',

    `body`           TEXT            NULL COMMENT 'Issue content',
    `comments`       INT UNSIGNED    NOT NULL NOT NULL DEFAULT 0 COMMENT 'Number of comments',
    CONSTRAINT PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARSET = `utf8mb4`
  COLLATE = `utf8mb4_general_ci`;

ALTER TABLE `issue`
    ADD INDEX (`id`);

ALTER TABLE `issue`
    ADD INDEX (`repo_full_name`, `issue_number`);

ALTER TABLE `issue`
    ADD INDEX (`repo_full_name`, `created_at`);