CREATE TABLE IF NOT EXISTS public.task (id VARCHAR(36) PRIMARY KEY, titulo VARCHAR(255), descripcion VARCHAR(255) NOT NULL, estado BOOLEAN NOT NULL);