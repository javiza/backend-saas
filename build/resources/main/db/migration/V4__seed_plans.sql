-- Planes de ejemplo para no arrancar con el catálogo de paquetes vacío.
-- El admin puede editarlos/desactivarlos o crear otros desde /admin/planes.

INSERT INTO plans (id, code, name, description, price, billing_cycle, trial_days, active, created_at)
VALUES (
    gen_random_uuid(),
    'STARTER',
    'Starter',
    'Prueba gratis de 15 días con Control de Acceso. Ideal para empezar.',
    9990.00,
    'MENSUAL',
    15,
    TRUE,
    now()
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO plans (id, code, name, description, price, billing_cycle, trial_days, active, created_at)
VALUES (
    gen_random_uuid(),
    'BUSINESS',
    'Business',
    'Paquete completo: Control de Acceso + Agencia de Turismo, con 15 días de prueba.',
    19990.00,
    'MENSUAL',
    15,
    TRUE,
    now()
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO plan_applications (plan_id, application_id)
SELECT p.id, a.id
FROM plans p, applications a
WHERE p.code = 'STARTER' AND a.code = 'CONTROL_ACCESS'
ON CONFLICT DO NOTHING;

INSERT INTO plan_applications (plan_id, application_id)
SELECT p.id, a.id
FROM plans p, applications a
WHERE p.code = 'BUSINESS' AND a.code IN ('CONTROL_ACCESS', 'TURISMO')
ON CONFLICT DO NOTHING;
