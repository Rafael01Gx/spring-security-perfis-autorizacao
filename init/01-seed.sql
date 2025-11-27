CREATE TABLE IF NOT EXISTS usuarios(
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,

    PRIMARY KEY(id)
);

INSERT INTO usuarios(nome, email, senha) VALUES
  ('Joao', 'joao@email.com', '$2y$10$GE/i8ZvFAPuRU8Av7Bq4sO3/PbSYWon2MG2oscE2B7nxPwCEGI2yu'),
  ('Maria', 'maria@email.com', '$2y$10$HgctbwaDJxiEqvf0Ddo.6ufv4feKiVdpoTWLw4qpfh5wtv9doAGN2');
