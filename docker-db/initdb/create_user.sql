-- Crear el usuario user_Test con contraseña passwort_Test
CREATE USER "user_Test" WITH PASSWORD 'password_Test';

-- Dar permisos al usuario user_Test sobre el esquema LITERALURA
GRANT USAGE ON SCHEMA "LITERALURA" TO "user_Test";
GRANT CREATE ON SCHEMA "LITERALURA" TO "user_Test";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "LITERALURA" TO "user_Test";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "LITERALURA" TO "user_Test";

-- Dar permisos al usuario user_Test para crear objetos dentro del esquema LITERALURA
ALTER DEFAULT PRIVILEGES IN SCHEMA "LITERALURA" GRANT ALL ON TABLES TO "user_Test";
ALTER DEFAULT PRIVILEGES IN SCHEMA "LITERALURA" GRANT ALL ON SEQUENCES TO "user_Test";