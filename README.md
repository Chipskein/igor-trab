# 📚 Catálogo de Livros — Java + JDBC + JPA/Hibernate + Redis

Trabalho 1 — Sistema de Catálogo de Livros  
Demonstra JDBC, JPA/Hibernate, cache Redis e comparação de desempenho.

---

## 🛠️ Pré-requisitos

| Ferramenta | Versão mínima |
|------------|---------------|
| Java (JDK) | 17            |
| Maven      | 3.8           |
| PostgreSQL  | 14            |
| Redis      | 6             |

---

## ⚙️ Configuração

### 1. Banco de dados PostgreSQL

```bash
# Criar banco
psql -U postgres -c "CREATE DATABASE catalogo_livros;"

# Criar tabela e inserir dados de exemplo
psql -U postgres -d catalogo_livros -f init.sql
```

### 2. Redis

```bash
# Iniciar Redis (Linux/Mac)
redis-server

# Windows — via WSL ou Docker:
docker run -d -p 6379:6379 redis:alpine
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

## ▶️ Executar

```bash
# Compilar e empacotar
mvn clean package -q

# Executar
java -jar target/catalogo-livros-1.0-SNAPSHOT.jar
```

Acesse a interface em: **http://localhost:7000**

---

## 📁 Estrutura do projeto

```
catalogo-livros/
├── pom.xml
├── init.sql                          ← Script SQL de inicialização
└── src/main/
    ├── java/
    │   ├── Main.java                 ← Servidor Javalin + rotas REST
    │   ├── config/
    │   │   ├── DatabaseConfig.java   ← Configuração JDBC (PostgreSQL)
    │   │   └── RedisConfig.java      ← Configuração Jedis (Redis)
    │   ├── model/
    │   │   └── Livro.java            ← Entidade JPA (@Entity, @Id, @Column)
    │   ├── jdbc/
    │   │   └── LivroJDBC.java        ← PARTE 1: acesso via JDBC puro
    │   ├── repository/
    │   │   └── LivroRepository.java  ← PARTE 2: JPA/Hibernate (listarTodos, buscarPorAutor)
    │   ├── cache/
    │   │   └── LivroCache.java       ← PARTE 3: cache Redis com fallback JDBC
    │   └── benchmark/
    │       └── BenchmarkService.java ← PARTE 4: comparação de desempenho
    └── resources/
        ├── META-INF/
        │   └── persistence.xml       ← Configuração JPA/Hibernate
        └── web/
            └── index.html            ← Interface web (Bootstrap 5)
```

---

## 🌐 API REST

| Método | Endpoint                        | Descrição                        |
|--------|---------------------------------|----------------------------------|
| GET    | `/api/livros`                   | Listar todos (JPA)               |
| GET    | `/api/livros/jdbc`              | Listar todos (JDBC puro)         |
| GET    | `/api/livros/cache`             | Listar todos (Redis + JDBC)      |
| GET    | `/api/livros/{id}`              | Buscar por id                    |
| GET    | `/api/livros/autor/{autor}`     | Buscar por autor (JPA, LIKE)     |
| GET    | `/api/livros/categoria/{cat}`   | Buscar por categoria             |
| POST   | `/api/livros`                   | Criar livro                      |
| PUT    | `/api/livros/{id}`              | Atualizar livro                  |
| DELETE | `/api/livros/{id}`              | Deletar livro                    |
| GET    | `/api/cache/status`             | Status do cache Redis            |
| DELETE | `/api/cache`                    | Invalidar cache                  |
| GET    | `/api/benchmark`                | Executar teste de desempenho     |

---

## 📊 Partes do trabalho

### Parte 1 — JDBC (`LivroJDBC.java`)
- Usa `DriverManager.getConnection()` diretamente
- Gerencia `Connection`, `PreparedStatement` e `ResultSet` manualmente
- Método principal: `listarLivros()`

### Parte 2 — JPA/Hibernate (`Livro.java` + `LivroRepository.java`)
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`
- `EntityManager` criado via `Persistence.createEntityManagerFactory()`
- Métodos: `listarTodos()`, `buscarPorAutor(String autor)`

### Parte 3 — Cache Redis (`LivroCache.java`)
- Jedis com pool de conexões (`JedisPool`)
- Fluxo: Redis → (miss) PostgreSQL → armazena no Redis → retorna
- Chave: `livros:todos`, TTL: 60 segundos
- Invalidação automática em toda operação de escrita

### Parte 4 — Benchmark (`BenchmarkService.java`)
- Mede `System.currentTimeMillis()` antes e depois de cada consulta
- Compara JDBC direto vs. Redis cache (1ª execução e 2ª execução)
- Saída no console:
  ```
  Tempo sem cache          : X ms
  Tempo com cache (1ª vez) : X ms
  Tempo com cache (2ª vez) : Y ms
  Ganho de desempenho      : Z%
  ```
