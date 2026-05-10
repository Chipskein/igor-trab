package model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "livro")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "autor", nullable = false, length = 150)
    private String autor;

    @Column(name = "ano_publicacao")
    @JsonProperty("ano_publicacao")
    private Integer anoPublicacao;

    @Column(name = "categoria", length = 100)
    private String categoria;

    public Livro() {}

    public Livro(String titulo, String autor, Integer anoPublicacao, String categoria) {
        this.titulo        = titulo;
        this.autor         = autor;
        this.anoPublicacao = anoPublicacao;
        this.categoria     = categoria;
    }

    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }

    public String getTitulo()                { return titulo; }
    public void setTitulo(String titulo)     { this.titulo = titulo; }

    public String getAutor()                 { return autor; }
    public void setAutor(String autor)       { this.autor = autor; }

    public Integer getAnoPublicacao()                     { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao)   { this.anoPublicacao = anoPublicacao; }

    public String getCategoria()                { return categoria; }
    public void setCategoria(String categoria)  { this.categoria = categoria; }

    @Override
    public String toString() {
        return String.format(
            "Livro{id=%d, titulo='%s', autor='%s', ano=%d, categoria='%s'}",
            id, titulo, autor, anoPublicacao, categoria
        );
    }
}
