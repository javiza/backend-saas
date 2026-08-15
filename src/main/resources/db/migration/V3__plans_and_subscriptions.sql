-- Planes/paquetes que el admin de plataforma puede inventar (nombre,
-- precio, ciclo de facturación, días de prueba gratis) y las apps que
-- incluye cada uno, más el historial de contrataciones por empresa
-- (trial -> activa -> vencida/cancelada).

CREATE TABLE plans (
    id            UUID PRIMARY KEY,
    code          VARCHAR(50) NOT NULL,
    name          VARCHAR(150) NOT NULL,
    description   VARCHAR(500) NOT NULL,
    price         NUMERIC(12,2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    trial_days    INTEGER NOT NULL DEFAULT 0,
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL,
    CONSTRAINT uk_plans_code UNIQUE (code)
);

CREATE TABLE plan_applications (
    plan_id        UUID NOT NULL,
    application_id UUID NOT NULL,
    PRIMARY KEY (plan_id, application_id),
    CONSTRAINT fk_plan_applications_plan
        FOREIGN KEY (plan_id) REFERENCES plans (id),
    CONSTRAINT fk_plan_applications_application
        FOREIGN KEY (application_id) REFERENCES applications (id)
);

CREATE TABLE company_subscriptions (
    id                  UUID PRIMARY KEY,
    company_id          UUID NOT NULL,
    plan_id             UUID NOT NULL,
    status              VARCHAR(20) NOT NULL,
    started_at          TIMESTAMP NOT NULL,
    trial_ends_at       TIMESTAMP NULL,
    current_period_end  TIMESTAMP NULL,
    canceled_at         TIMESTAMP NULL,
    created_at          TIMESTAMP NOT NULL,
    CONSTRAINT fk_company_subscriptions_company
        FOREIGN KEY (company_id) REFERENCES companies (id),
    CONSTRAINT fk_company_subscriptions_plan
        FOREIGN KEY (plan_id) REFERENCES plans (id)
);

CREATE INDEX idx_company_subscriptions_company ON company_subscriptions (company_id);
CREATE INDEX idx_company_subscriptions_status ON company_subscriptions (status);
