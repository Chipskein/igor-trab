# Trabalho 1 – Sistema de Catálogo de Livros com Java, ORM e Redis (5 pontos)


# Rodar

## Configuração

### 1. Banco de dados PostgreSQL

```bash
psql -U postgres -c "CREATE DATABASE catalogo_livros;"
psql -U postgres -d catalogo_livros -f init.sql
```

### 2. Redis

```bash
redis-server
```

### 3. Ajuste as credenciais (se necessário)

Edite **`src/main/java/config/DatabaseConfig.java`**:
```java
public static final String URL     = "jdbc:postgresql://localhost:5432/catalogo_livros";
public static final String USUARIO = "postgres";
public static final String SENHA   = "postgres";
```

Edite **`src/main/java/config/RedisConfig.java`** se o Redis estiver em outro host/porta.

Edite **`src/main/resources/META-INF/persistence.xml`** com as mesmas credenciais para JPA.

---

## Executar

```bash
mvn compile exec:java -Dexec.mainClass=Main
```

Acesse a interface em: **http://localhost:7000**

## Objetivo

Desenvolver uma aplicação em **Java** para gerenciar um **catálogo de livros**, utilizando diferentes tecnologias de acesso a dados:

* **JDBC**
* **JPA / Hibernate (ORM)**
* **Redis para cache**

O objetivo é demonstrar **diferentes abordagens de persistência e otimização de consultas**.

---

# Modelo de dados

Tabela principal:

```
livro
```

Campos:

```
id
titulo
autor
ano_publicacao
categoria
```

---

# Parte 1 – Consulta usando JDBC (1,5 pontos)

Criar uma classe:

```
LivroJDBC
```

Implementar o método:

```
listarLivros()
```

A consulta deve retornar:

```
id
titulo
autor
ano_publicacao
categoria
```

Utilizar **JDBC puro** para acessar o banco PostgreSQL.

---

# Parte 2 – Mapeamento ORM com JPA / Hibernate (1,5 pontos)

Criar a entidade:

```
Livro
```

Utilizando anotações:

```
@Entity
@Id
@Column
```

Criar um repositório:

```
LivroRepository
```

Com os métodos:

```
listarTodos()

buscarPorAutor(String autor)
```

Utilizando **JPA/Hibernate**.

---

# Parte 3 – Cache de consultas com Redis (1 ponto)

Criar uma classe:

```
LivroCache
```

Implementar método:

```
listarLivros()
```

Regra:

1. verificar se os dados estão no **Redis**
2. caso não estejam, consultar no **PostgreSQL**
3. armazenar no Redis
4. retornar os dados

---

# Parte 4 – Teste de desempenho (1 ponto)

Executar a consulta de listagem de livros:

```
listarLivros()
```

Comparar:

* execução **sem cache**
* execução **com Redis**

Mostrar no console:

```
Tempo sem cache: X ms
Tempo com cache: Y ms
```

---

# Estrutura sugerida do projeto

```
src/

model/
Livro.java

jdbc/
LivroJDBC.java

repository/
LivroRepository.java

cache/
LivroCache.java

Main.java
```

---

# Tecnologias obrigatórias

* **Java**
* **PostgreSQL**
* **JDBC**
* **JPA**
* **Hibernate**
* **Redis**

---

# Critérios de avaliação

| Item                         | Pontos |
| ---------------------------- | ------ |
| Implementação JDBC           | 1,5    |
| Mapeamento JPA / Hibernate   | 1,5    |
| Implementação do cache Redis | 1,0    |
| Comparação de desempenho     | 1,0    |

Total: **5 pontos**
