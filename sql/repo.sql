USE `sentisamoyed`;

# DROP TABLE IF EXISTS `repo`;
CREATE TABLE `repo`
(
    `id`        BIGINT UNSIGNED NOT NULL COMMENT 'Repo ID',
    `owner`     VARCHAR(100) NOT NULL COMMENT 'The owner of the repo. Can be an org.',
    `name`      VARCHAR(50)  NOT NULL COMMENT 'Repo name',
    `full_name` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Repo full name, e.g. apache/Flink',
    `html_url`  VARCHAR(100) NOT NULL COMMENT 'Repo URL',
    CONSTRAINT PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARSET = `utf8mb4`
  COLLATE = `utf8mb4_general_ci`;

ALTER TABLE `repo`
    ADD INDEX (`id`);

ALTER TABLE `repo`
    ADD INDEX (`full_name`)