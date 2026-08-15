-- Tarjeta guardada (inscripción Oneclick de Transbank) por empresa: se usa
-- para cobrar automáticamente cada renovación de suscripción sin que el
-- cliente tenga que volver a ingresar datos de la tarjeta.
CREATE TABLE payment_methods (
    id              UUID PRIMARY KEY,
    company_id      UUID NOT NULL,
    tbk_user        VARCHAR(64) NOT NULL,
    username        VARCHAR(64) NOT NULL,
    card_type       VARCHAR(30),
    card_last4      VARCHAR(4),
    created_at      TIMESTAMP NOT NULL,
    CONSTRAINT uk_payment_methods_company UNIQUE (company_id),
    CONSTRAINT fk_payment_methods_company
        FOREIGN KEY (company_id) REFERENCES companies (id)
);

-- Historial de cobros (Oneclick authorize) asociados a cada suscripción:
-- el primer cobro al contratar un plan pago, y cada cobro de renovación
-- que dispara el cron de CompanySubscriptionService.
CREATE TABLE payments (
    id                      UUID PRIMARY KEY,
    company_subscription_id UUID NOT NULL,
    buy_order               VARCHAR(64) NOT NULL,
    amount                  NUMERIC(12,2) NOT NULL,
    status                  VARCHAR(20) NOT NULL,
    authorization_code      VARCHAR(20),
    response_code           INTEGER,
    transaction_date        TIMESTAMP,
    created_at              TIMESTAMP NOT NULL,
    CONSTRAINT uk_payments_buy_order UNIQUE (buy_order),
    CONSTRAINT fk_payments_subscription
        FOREIGN KEY (company_subscription_id) REFERENCES company_subscriptions (id)
);

CREATE INDEX idx_payments_subscription ON payments (company_subscription_id);
