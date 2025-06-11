CREATE TABLE session
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     datetime NULL,
    updated_at     datetime NULL,
    is_deleted     BIT(1) NULL,
    token          VARCHAR(255) NULL,
    expiry_date    datetime NULL,
    user_id        BIGINT NULL,
    session_status SMALLINT NULL,
    CONSTRAINT pk_session PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    is_deleted BIT(1) NULL,
    `role`     VARCHAR(255) NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (id)
);

CREATE TABLE user_table
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    created_at          datetime NULL,
    updated_at          datetime NULL,
    is_deleted          BIT(1) NULL,
    name                VARCHAR(255) NULL,
    email               VARCHAR(255) NULL,
    password            VARCHAR(255) NULL,
    phone_number        VARCHAR(255) NULL,
    otp                 VARCHAR(255) NULL,
    otp_expiry          datetime NULL,
    verification_status SMALLINT NULL,
    CONSTRAINT pk_user_table PRIMARY KEY (id)
);

CREATE TABLE user_table_roles
(
    user_id  BIGINT NOT NULL,
    roles_id BIGINT NOT NULL
);

ALTER TABLE session
    ADD CONSTRAINT FK_SESSION_ON_USER FOREIGN KEY (user_id) REFERENCES user_table (id);

ALTER TABLE user_table_roles
    ADD CONSTRAINT fk_usetabrol_on_roles FOREIGN KEY (roles_id) REFERENCES user_roles (id);

ALTER TABLE user_table_roles
    ADD CONSTRAINT fk_usetabrol_on_user FOREIGN KEY (user_id) REFERENCES user_table (id);