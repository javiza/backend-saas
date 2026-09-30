-- El plan BUSINESS (seedeado en V4) no se usa para arrancar: se elimina
-- por completo. Se borra en orden por las FKs: primero cualquier
-- suscripción de alguna empresa a este plan, después el vínculo con sus
-- apps, y por último el plan.

DELETE FROM company_subscriptions
WHERE plan_id IN (SELECT id FROM plans WHERE code = 'BUSINESS');

DELETE FROM plan_applications
WHERE plan_id IN (SELECT id FROM plans WHERE code = 'BUSINESS');

DELETE FROM plans
WHERE code = 'BUSINESS';
