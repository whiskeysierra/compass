CREATE TABLE dimension (
    id TEXT PRIMARY KEY,
    schema JSONB NOT NULL,
    relation TEXT NOT NULL,
    description TEXT NOT NULL
);

INSERT INTO dimension(id, schema, relation, description)
    -- underscore is a reserved char that is not allowed via the API
    VALUES ('_key', '{"type": "string"}'::JSONB, '=', 'First, fixed dimension that is always present.');

CREATE TABLE value (
  dimensions JSONB NOT NULL, -- map of dimension id to dimension value
  value JSONB NOT NULL
);

CREATE INDEX ON value((dimensions->>'_key'));