-- The 1. Dimension!
CREATE TABLE key (
  id TEXT PRIMARY KEY,
  schema JSONB NOT NULL,
  description TEXT NOT NULL
);

CREATE TABLE dimension (
  id TEXT PRIMARY KEY,
  priority SERIAL UNIQUE DEFERRABLE NOT NULL,
  schema JSONB NOT NULL,
  relation TEXT NOT NULL,
  description TEXT NOT NULL
);

CREATE TABLE value (
  key TEXT NOT NULL REFERENCES key(id),
  dimensions JSONB NOT NULL, -- map of dimension id to dimension value
  value JSONB NOT NULL
);