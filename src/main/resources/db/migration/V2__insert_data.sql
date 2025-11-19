INSERT INTO tipos_camisa (nombre, descripcion) VALUES
                                                   ('Casual', 'Camisa casual para uso diario'),
                                                   ('Formal', 'Camisa para eventos formales'),
                                                   ('Deportiva', 'Camisa para hacer deporte');

-- Insertar camisas
INSERT INTO camisas (nombre, talla, color, precio, imagen_url, lat, lng, tipo_id) VALUES
                                                                                      ('Camisa Azul', 'L', 'Azul', 25.99, NULL, NULL, NULL, 1),
                                                                                      ('Camisa Blanca', 'M', 'Blanco', 30.50, NULL, NULL, NULL, 2),
                                                                                      ('Camisa Deportiva Roja', 'S', 'Rojo', 28.75, NULL, NULL, NULL, 3);