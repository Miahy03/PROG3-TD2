DROP TABLE IF EXISTS dish;

-- Création de la table dish
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,

    -- prix de vente (peut être NULL si non défini)
    price DOUBLE PRECISION,

    -- COÛT DES INGRÉDIENTS 
    ingredients_cost DOUBLE PRECISION NOT NULL
);


INSERT INTO dish (name, price, ingredients_cost) VALUES
('Salade fraise', 2000, 1500),
('Poulet grillé', 6000, 3500),
('Riz au légume', NULL, 1200),
('Gâteau au chocolat', NULL, 2000),
('Salade de fruit', NULL, 1000);
