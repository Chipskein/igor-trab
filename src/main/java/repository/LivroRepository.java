package repository;

import jakarta.persistence.*;
import model.Livro;

import java.util.List;

public class LivroRepository {

    private static final EntityManagerFactory EMF =
        Persistence.createEntityManagerFactory("catalogo-livros");

    public List<Livro> listarTodos() {
        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<Livro> query = em.createQuery(
                "SELECT l FROM Livro l ORDER BY l.titulo", Livro.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Livro> buscarPorAutor(String autor) {
        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<Livro> query = em.createQuery(
                "SELECT l FROM Livro l WHERE LOWER(l.autor) LIKE LOWER(:autor) ORDER BY l.titulo",
                Livro.class
            );
            query.setParameter("autor", "%" + autor + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Livro salvar(Livro livro) {
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(livro);
            em.getTransaction().commit();
            return livro;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao salvar livro", e);
        } finally {
            em.close();
        }
    }

    public Livro atualizar(Livro livro) {
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            Livro atualizado = em.merge(livro);
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao atualizar livro", e);
        } finally {
            em.close();
        }
    }

    public boolean deletar(Long id) {
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            Livro livro = em.find(Livro.class, id);
            if (livro == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(livro);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erro ao deletar livro", e);
        } finally {
            em.close();
        }
    }

    public Livro buscarPorId(Long id) {
        EntityManager em = EMF.createEntityManager();
        try {
            return em.find(Livro.class, id);
        } finally {
            em.close();
        }
    }

    public List<Livro> buscarPorCategoria(String categoria) {
        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<Livro> query = em.createQuery(
                "SELECT l FROM Livro l WHERE LOWER(l.categoria) LIKE LOWER(:cat) ORDER BY l.titulo",
                Livro.class
            );
            query.setParameter("cat", "%" + categoria + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public static void fechar() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
