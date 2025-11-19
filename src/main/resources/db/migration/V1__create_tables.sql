CREATE TABLE tipos_camisa (
                              id BIGSERIAL PRIMARY KEY,
                              nombre VARCHAR(100) UNIQUE NOT NULL,
                              descripcion TEXT
);

CREATE TABLE camisas (
                         id BIGSERIAL PRIMARY KEY,
                         nombre VARCHAR(120) NOT NULL,
                         talla VARCHAR(10),
                         color VARCHAR(50),
                         precio NUMERIC(10,2),
                         imagen_url TEXT,
                         lat DOUBLE PRECISION,
                         lng DOUBLE PRECISION,
                         tipo_id BIGINT NOT NULL REFERENCES tipos_camisa(id)
);