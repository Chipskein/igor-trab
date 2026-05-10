import benchmark.BenchmarkService;
import cache.LivroCache;
import config.RedisConfig;
import io.javalin.Javalin;
import jdbc.LivroJDBC;
import model.Livro;
import repository.LivroRepository;

import java.util.List;
import java.util.Map;

public class Main {

    private static final LivroJDBC        livroJDBC   = new LivroJDBC();
    private static final LivroRepository  livroRepo   = new LivroRepository();
    private static final LivroCache       livroCache  = new LivroCache();
    private static final BenchmarkService benchmark   = new BenchmarkService();

    public static void main(String[] args) {

        livroJDBC.criarTabelaSeNaoExistir();

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/web");
            config.bundledPlugins.enableCors(cors ->
                cors.addRule(rule -> rule.anyHost())
            );
        }).start(7000);

        System.out.println("✅  Servidor iniciado em http://localhost:7000");

        app.get("/api/livros", ctx -> {
            List<Livro> livros = livroRepo.listarTodos();
            ctx.json(livros);
        });

        app.get("/api/livros/jdbc", ctx -> {
            List<Livro> livros = livroJDBC.listarLivros();
            ctx.json(livros);
        });

        app.get("/api/livros/cache", ctx -> {
            List<Livro> livros = livroCache.listarLivros();
            ctx.json(livros);
        });

        app.get("/api/livros/autor/{autor}", ctx -> {
            String autor = ctx.pathParam("autor");
            List<Livro> livros = livroRepo.buscarPorAutor(autor);
            ctx.json(livros);
        });

        app.get("/api/livros/categoria/{categoria}", ctx -> {
            String categoria = ctx.pathParam("categoria");
            List<Livro> livros = livroRepo.buscarPorCategoria(categoria);
            ctx.json(livros);
        });

        app.get("/api/livros/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Livro livro = livroRepo.buscarPorId(id);
            if (livro != null) ctx.json(livro);
            else ctx.status(404).json(Map.of("erro", "Livro não encontrado"));
        });

        app.post("/api/livros", ctx -> {
            Livro livro = ctx.bodyAsClass(Livro.class);
            Livro salvo = livroRepo.salvar(livro);
            livroCache.invalidarCache();
            ctx.status(201).json(salvo);
        });

        app.put("/api/livros/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            Livro livro = ctx.bodyAsClass(Livro.class);
            livro.setId(id);
            Livro atualizado = livroRepo.atualizar(livro);
            livroCache.invalidarCache();
            ctx.json(atualizado);
        });

        app.delete("/api/livros/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            boolean ok = livroRepo.deletar(id);
            livroCache.invalidarCache();
            if (ok) ctx.json(Map.of("mensagem", "Livro removido com sucesso"));
            else    ctx.status(404).json(Map.of("erro", "Livro não encontrado"));
        });

        app.get("/api/cache/status", ctx -> {
            ctx.json(Map.of(
                "ativo",        livroCache.cacheAtivo(),
                "ttlRestante",  livroCache.ttlRestante()
            ));
        });

        app.delete("/api/cache", ctx -> {
            livroCache.invalidarCache();
            ctx.json(Map.of("mensagem", "Cache invalidado com sucesso"));
        });

        app.get("/api/benchmark", ctx -> {
            BenchmarkService.ResultadoBenchmark resultado = benchmark.executar();
            ctx.json(resultado);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LivroRepository.fechar();
            RedisConfig.fecharPool();
            System.out.println("Aplicação encerrada.");
        }));
    }
}
