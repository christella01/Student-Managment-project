-- Run: CREATE DATABASE student_management;  (then connect to it)
CREATE TABLE IF NOT EXISTS students (
    id        SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    age       INTEGER NOT NULL CHECK (age > 18),
    email     VARCHAR(150) NOT NULL UNIQUE
);
