-- CREATE DATABASE catalogo_livros;
-- \c catalogo_livros

CREATE TABLE IF NOT EXISTS livro (
    id              SERIAL PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    autor           VARCHAR(150) NOT NULL,
    ano_publicacao  INTEGER,
    categoria       VARCHAR(100)
);

INSERT INTO livro (titulo, autor, ano_publicacao, categoria) VALUES
  ('Dom Casmurro',                  'Machado de Assis',    1899, 'Romance'),
  ('Memórias Póstumas de Brás Cubas','Machado de Assis',   1881, 'Romance'),
  ('Grande Sertão: Veredas',        'João Guimarães Rosa', 1956, 'Romance'),
  ('O Cortiço',                     'Aluísio Azevedo',     1890, 'Romance'),
  ('Capitães da Areia',             'Jorge Amado',         1937, 'Romance'),
  ('1984',                          'George Orwell',       1949, 'Ficção Científica'),
  ('Admirável Mundo Novo',          'Aldous Huxley',       1932, 'Ficção Científica'),
  ('Duna',                          'Frank Herbert',       1965, 'Ficção Científica'),
  ('O Senhor dos Anéis',            'J.R.R. Tolkien',      1954, 'Fantasia'),
  ('Harry Potter e a Pedra Filosofal','J.K. Rowling',      1997, 'Fantasia'),
  ('Clean Code',                    'Robert C. Martin',    2008, 'Técnico'),
  ('The Pragmatic Programmer',      'Andrew Hunt',         1999, 'Técnico'),
  ('Design Patterns',               'Gang of Four',        1994, 'Técnico'),
  ('Sapiens',                       'Yuval Noah Harari',   2011, 'História'),
  ('Uma Breve História do Tempo',   'Stephen Hawking',     1988, 'Ciência')
ON CONFLICT DO NOTHING;

SELECT id, titulo, autor, ano_publicacao, categoria FROM livro ORDER BY id;
