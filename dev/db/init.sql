-- CREATE DATABASE tg_bot_db;
-- CREATE USER tg_bot_db_admin WITH PASSWORD 'password';
-- GRANT ALL PRIVILEGES ON DATABASE "tg_bot_db" to tg_bot_db_admin;

DO
$do$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE  rolname = 'tg_bot_db_admin') THEN
      CREATE USER tg_bot_db_admin NOCREATEDB CREATEROLE NOSUPERUSER PASSWORD 'tg_bot_db_admin';
END IF;
END
$do$;

DO
$do$
BEGIN
   IF NOT EXISTS ( SELECT FROM pg_roles WHERE  rolname = 'tg_bot_db_client') THEN
      CREATE USER tg_bot_db_client NOCREATEDB NOCREATEROLE NOSUPERUSER PASSWORD 'tg_bot_db_client';
END IF;
END
$do$;

CREATE DATABASE tg_bot_db OWNER tg_bot_db_admin ENCODING 'UTF8' TEMPLATE='template0' CONNECTION LIMIT 1000;

\connect tg_bot_db
-- CREATE SCHEMA liquibase AUTHORIZATION task_tracker_db_admin;
CREATE SCHEMA tg_bot_db AUTHORIZATION tg_bot_db_admin;

ALTER DEFAULT PRIVILEGES FOR USER tg_bot_db_admin IN SCHEMA tg_bot_db GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO tg_bot_db_client;
ALTER DEFAULT PRIVILEGES FOR ROLE tg_bot_db_admin GRANT SELECT,INSERT,UPDATE,DELETE ON TABLES TO tg_bot_db_client;
ALTER DEFAULT PRIVILEGES FOR ROLE tg_bot_db_admin GRANT USAGE ON SCHEMAS TO tg_bot_db_client;
GRANT USAGE ON SCHEMA tg_bot_db TO tg_bot_db_client;

ALTER DATABASE tg_bot_db SET search_path TO tg_bot_db;
ALTER USER tg_bot_db_client SET search_path TO 'tg_bot_db';