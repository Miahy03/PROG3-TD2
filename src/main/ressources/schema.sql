DROP TABLE IF EXISTS dish;

CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,

    -- AJOUT DEMANDÉ PAR LE SUJET
    -- Prix de vente (peut être NULL si non défini)
    price DOUBLE PRECISION,

 -- Coût des ingrédients (déjà présent dans le TD initial)
    ingredients_cost DOUBLE PRECISION NOT NULL
);
