package benchmark;

import cache.LivroCache;
import jdbc.LivroJDBC;
import model.Livro;

import java.util.List;

public class BenchmarkService {

    private final LivroJDBC  livroJDBC;
    private final LivroCache livroCache;

    public BenchmarkService() {
        this.livroJDBC  = new LivroJDBC();
        this.livroCache = new LivroCache();
    }

    public ResultadoBenchmark executar() {
        System.out.println("\n========================================");
        System.out.println("  BENCHMARK – Comparação de desempenho  ");
        System.out.println("========================================\n");

        System.out.println("[ 1/4 ] Medindo tempo SEM cache (JDBC direto)...");
        long semCacheMs = medirSemCache();

        System.out.println("\n[ 2/4 ] Invalidando cache e medindo PRIMEIRA execução com Redis...");
        livroCache.invalidarCache();
        long primeiraComCacheMs = medirComCache();

        System.out.println("\n[ 3/4 ] Medindo SEGUNDA execução com Redis (dados já em cache)...");
        long comCacheMs = medirComCache();

        double ganho = semCacheMs > 0
            ? ((double)(semCacheMs - comCacheMs) / semCacheMs) * 100.0
            : 0;

        System.out.println("\n========================================");
        System.out.printf("  Tempo sem cache          : %4d ms%n", semCacheMs);
        System.out.printf("  Tempo com cache (1ª vez) : %4d ms%n", primeiraComCacheMs);
        System.out.printf("  Tempo com cache (2ª vez) : %4d ms%n", comCacheMs);
        System.out.printf("  Ganho de desempenho      : %.1f%%%n",  ganho);
        System.out.println("========================================\n");

        return new ResultadoBenchmark(semCacheMs, primeiraComCacheMs, comCacheMs, ganho);
    }

    private long medirSemCache() {
        long inicio = System.currentTimeMillis();
        List<Livro> livros = livroJDBC.listarLivros();
        long tempo = System.currentTimeMillis() - inicio;
        System.out.printf("   → %d livro(s) retornado(s) em %d ms%n", livros.size(), tempo);
        return tempo;
    }

    private long medirComCache() {
        long inicio = System.currentTimeMillis();
        List<Livro> livros = livroCache.listarLivros();
        long tempo = System.currentTimeMillis() - inicio;
        System.out.printf("   → %d livro(s) retornado(s) em %d ms%n", livros.size(), tempo);
        return tempo;
    }

    public static class ResultadoBenchmark {
        public final long   semCacheMs;
        public final long   primeiraComCacheMs;
        public final long   comCacheMs;
        public final double ganhoPercent;

        public ResultadoBenchmark(long semCacheMs, long primeiraComCacheMs,
                                   long comCacheMs, double ganhoPercent) {
            this.semCacheMs          = semCacheMs;
            this.primeiraComCacheMs  = primeiraComCacheMs;
            this.comCacheMs          = comCacheMs;
            this.ganhoPercent        = ganhoPercent;
        }
    }
}
