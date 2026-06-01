package br.mackenzie.biblioteca.model;

public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private int ano;
    private String isbn;

    public Livro(int id, String titulo, String autor, int ano, String isbn) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.isbn = isbn;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAno() {
        return ano;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", ano=" + ano +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
