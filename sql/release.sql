USE sentisamoyed;

# DROP TABLE IF EXISTS `release`;

CREATE TABLE `release`
(
    id               BIGINT UNSIGNED NOT NULL COMMENT 'Release ID',
    repo_full_name   VARCHAR(100)    NOT NULL COMMENT 'Repo full name',
    tag_name         VARCHAR(100)    NOT NULL UNIQUE COMMENT 'Release tag name',

    created_at       DATETIME        NOT NULL COMMENT 'Create time',
    updated_at       DATETIME        NOT NULL COMMENT 'Update time',

    sum_hitherto     INT             NULL COMMENT 'Sum of scores of the repo up to this release',
    count_hitherto   INT             NULL COMMENT 'Count of rows of issues up to this release',

    pos_cnt_hitherto INT             NULL COMMENT 'Sum of positive scores of the repo up to this release',
    neg_cnt_hitherto INT             NULL COMMENT 'Sum of negative scores of the repo up to this release',
    CONSTRAINT PRIMARY KEY (id)
) ENGINE = InnoDB
  CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

ALTER TABLE `release`
    ADD INDEX (id);

ALTER TABLE `release`
    ADD INDEX (repo_full_name, tag_name);

ALTER TABLE `release`
    ADD INDEX (repo_full_name, created_at);