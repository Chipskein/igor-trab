package cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.RedisConfig;
import jdbc.LivroJDBC;
import model.Livro;
import redis.clients.jedis.Jedis;

import java.util.List;

public class LivroCache {

    private static final String CACHE_KEY = "livros:todos";

    private static final int TTL_SEGUNDOS = 60;

    private final LivroJDBC    livroJDBC;
    private final ObjectMapper objectMapper;

    public LivroCache() {
        this.livroJDBC    = new LivroJDBC();
        this.objectMapper = new ObjectMapper();
    }

    public List<Livro> listarLivros() {
        try (Jedis jedis = RedisConfig.getJedis()) {

            String json = jedis.get(CACHE_KEY);

            if (json != null && !json.isEmpty()) {
                System.out.println("[Cache] HIT – dados recuperados do Redis.");
                return objectMapper.readValue(json, new TypeReference<List<Livro>>() {});
            }

        } catch (Exception e) {
            System.err.println("[Cache] Erro ao acessar Redis: " + e.getMessage());
        }

        System.out.println("[Cache] MISS – consultando PostgreSQL...");
        List<Livro> livros = livroJDBC.listarLivros();

        if (!livros.isEmpty()) {
            armazenarNoCache(livros);
        }

        return livros;
    }

    public void invalidarCache() {
        try (Jedis jedis = RedisConfig.getJedis()) {
            jedis.del(CACHE_KEY);
            System.out.println("[Cache] Cache invalidado.");
        } catch (Exception e) {
            System.err.println("[Cache] Erro ao invalidar cache: " + e.getMessage());
        }
    }

    public boolean cacheAtivo() {
        try (Jedis jedis = RedisConfig.getJedis()) {
            return jedis.exists(CACHE_KEY);
        } catch (Exception e) {
            return false;
        }
    }

    public long ttlRestante() {
        try (Jedis jedis = RedisConfig.getJedis()) {
            return jedis.ttl(CACHE_KEY);
        } catch (Exception e) {
            return -2;
        }
    }

    private void armazenarNoCache(List<Livro> livros) {
        try (Jedis jedis = RedisConfig.getJedis()) {
            String json = objectMapper.writeValueAsString(livros);
            jedis.setex(CACHE_KEY, TTL_SEGUNDOS, json);
            System.out.printf("[Cache] %d livro(s) armazenados no Redis (TTL=%ds).%n",
                livros.size(), TTL_SEGUNDOS);
        } catch (Exception e) {
            System.err.println("[Cache] Erro ao gravar no Redis: " + e.getMessage());
        }
    }
}
