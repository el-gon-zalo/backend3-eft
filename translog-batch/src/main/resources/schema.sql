CREATE DATABASE IF NOT EXISTS translog;

USE translog;

DROP TABLE IF EXISTS estadoFinanciero_processed;
DROP TABLE IF EXISTS interes_processed;
DROP TABLE IF EXISTS movimientoFinanciero_processed;


-- TABLA: estadoFinanciero_processed

CREATE TABLE IF NOT EXISTS estadoFinanciero_processed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id INT,
    fecha VARCHAR(50),
    transaccion VARCHAR(100),
    monto DOUBLE,
    descripcion VARCHAR(255)
);

-- TABLA: interes_processed

CREATE TABLE IF NOT EXISTS interes_processed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id INT,
    nombre VARCHAR(150),
    saldo_original DOUBLE,
    edad INT,
    tipo VARCHAR(50),
    interes_aplicado DOUBLE,
    saldo_final DOUBLE
);


-- TABLA: movimientoFinanciero_processed

CREATE TABLE IF NOT EXISTS movimientoFinanciero_processed (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha VARCHAR(50),
    monto DOUBLE,
    tipo VARCHAR(50),
    estado VARCHAR(50)
);