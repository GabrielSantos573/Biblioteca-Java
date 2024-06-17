import java.io.Serializable;

public class Livro implements Serializable {
    private String autor;
    private String nome;
    private String genero;
    private int exemplares;

    public Livro(String autor, String nome, String genero, int exemplares) {
        this.autor = autor;
        this.nome = nome;
        this.genero = genero;
        this.exemplares = exemplares;
    }

    // Getters and Setters
}
