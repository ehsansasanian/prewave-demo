CREATE TABLE edge
(
    from_id INTEGER NOT NULL,
    to_id   INTEGER NOT NULL,
    PRIMARY KEY (from_id, to_id),
    UNIQUE (to_id),
    CHECK (from_id != to_id)
);

-- CREATE INDEX idx_edge_from_id ON edge (from_id);
-- CREATE INDEX idx_edge_to_id ON edge (to_id);
