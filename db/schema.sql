CREATE TABLE IF NOT EXISTS article (
  id SERIAL PRIMARY KEY,
  name text NOT NULL,
  content text NOT NULL
);