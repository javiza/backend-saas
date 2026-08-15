-- Reemplaza a ApplicationDataInitializer.java: antes el catálogo base de
-- aplicaciones se insertaba en código Java al arrancar (CommandLineRunner).
-- Eso duplicaba la fuente de verdad (esquema en Flyway, datos en Java) y no
-- quedaba registrado en ningún historial de versiones. Ahora vive acá.

INSERT INTO applications (id, code, name, description, active, created_at)
VALUES (
    gen_random_uuid(),
    'CONTROL_ACCESS',
    'Control de Acceso',
    'Gestión de ingresos, salidas, visitantes y autorizaciones.',
    TRUE,
    now()
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO applications (id, code, name, description, active, created_at)
VALUES (
    gen_random_uuid(),
    'TURISMO',
    'Agencia de Turismo',
    'Gestión de contenido, clientes y operaciones de una agencia de turismo.',
    TRUE,
    now()
)
ON CONFLICT (code) DO NOTHING;
