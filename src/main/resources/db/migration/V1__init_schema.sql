-- Baseline: esquema actual de la plataforma, generado a partir de las
-- entidades JPA existentes (Company, Application, CompanyApplication, User).
-- A partir de esta migración, Hibernate deja de crear/alterar tablas
-- (ddl-auto: validate) y Flyway es la única fuente de verdad del esquema.

CREATE TABLE companies (
    id          UUID PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    CONSTRAINT uk_companies_email UNIQUE (email)
);

CREATE TABLE applications (
    id          UUID PRIMARY KEY,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(500) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL,
    CONSTRAINT uk_applications_code UNIQUE (code)
);

CREATE TABLE company_applications (
    id             UUID PRIMARY KEY,
    company_id     UUID NOT NULL,
    application_id UUID NOT NULL,
    active         BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at     TIMESTAMP NULL,
    created_at     TIMESTAMP NOT NULL,
    CONSTRAINT fk_company_applications_company
        FOREIGN KEY (company_id) REFERENCES companies (id),
    CONSTRAINT fk_company_applications_application
        FOREIGN KEY (application_id) REFERENCES applications (id),
    CONSTRAINT uk_company_applications_company_application
        UNIQUE (company_id, application_id)
);

CREATE TABLE users (
    id          UUID PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20) NOT NULL,
    company_id  UUID NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_company
        FOREIGN KEY (company_id) REFERENCES companies (id),
    CONSTRAINT uk_user_email_company
        UNIQUE (email, company_id)
);

CREATE INDEX idx_company_applications_company ON company_applications (company_id);
CREATE INDEX idx_users_company ON users (company_id);
