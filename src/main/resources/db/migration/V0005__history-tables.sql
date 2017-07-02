CREATE TABLE revision (
  id BIGSERIAL PRIMARY KEY,
  timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  "user" TEXT NOT NULL,
  comment TEXT NOT NULL
);

CREATE TYPE revision_type AS ENUM ('CREATE', 'UPDATE', 'DELETE');

CREATE TABLE key_revision (
  id TEXT NOT NULL,
  revision BIGINT NOT NULL REFERENCES revision(id),
  revision_type revision_type NOT NULL,
  schema JSONB NOT NULL,
  description TEXT NOT NULL,
  PRIMARY KEY (id, revision)
);

CREATE TABLE dimension_revision (
  id TEXT NOT NULL,
  revision BIGINT REFERENCES revision(id),
  revision_type revision_type NOT NULL,
  schema JSONB NOT NULL,
  relation TEXT NOT NULL,
  description TEXT NOT NULL,
  PRIMARY KEY (id, revision)
);

CREATE TABLE value_revision (
  id BIGSERIAL NOT NULL,
  revision BIGINT REFERENCES revision(id),
  revision_type revision_type NOT NULL,
  key_id TEXT NOT NULL,
  index BIGINT NOT NULL,
  value JSONB NOT NULL, -- adheres to key_revision.schema
  PRIMARY KEY (id, revision),
  -- TODO foreign key to key_revision(id) would be handy
  UNIQUE (key_id, revision, revision_type, index) -- we might have duplicate indexes due to deletions, hency the revision_type
);

CREATE TABLE value_dimension_revision (
  value_id BIGINT NOT NULL,
  value_revision BIGINT NOT NULL,
  dimension_id TEXT NOT NULL,
  dimension_value JSONB NOT NULL, -- adheres to dimension_revision.schema,
  FOREIGN KEY (value_id, value_revision) REFERENCES value_revision(id, revision),
  -- TODO foreign key to dimension_revision(id) would be handy
  UNIQUE (value_id, value_revision, dimension_id)
);