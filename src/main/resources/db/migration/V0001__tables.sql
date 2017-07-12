CREATE TABLE key (
  id TEXT PRIMARY KEY,
  schema JSONB NOT NULL,
  description TEXT NOT NULL
);

CREATE TABLE dimension (
  id TEXT PRIMARY KEY,
  schema JSONB NOT NULL,
  relation TEXT NOT NULL,
  description TEXT NOT NULL
);

CREATE TABLE value (
  id BIGSERIAL PRIMARY KEY,
  key_id TEXT NOT NULL REFERENCES key(id),
  index BIGSERIAL NOT NULL,
  value JSONB NOT NULL, -- adheres to key.schema
  UNIQUE (key_id, index) DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE value_dimension (
  value_id BIGINT NOT NULL REFERENCES value(id) ON DELETE CASCADE,
  dimension_id TEXT NOT NULL REFERENCES dimension(id),
  dimension_value JSONB NOT NULL, -- adheres to dimension.schema,
  UNIQUE (value_id, dimension_id) DEFERRABLE INITIALLY DEFERRED
);
