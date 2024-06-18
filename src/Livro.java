import java.io.Serializable;

// A classe Livro implementa a interface Serializable para permitir que seus objetos sejam convertidos em um fluxo de bytes,
// o que é necessário para operações como a gravação em arquivos ou a transmissão pela rede.
public class Livro implements Serializable {
    // Atributos privados da classe Livro
    private String autor;
    private String nome;
    private String genero;
    private int exemplares;

    // Construtor da classe Livro. Este construtor inicializa os atributos da classe com os valores fornecidos como parâmetros.
    public Livro(String autor, String nome, String genero, int exemplares) {
        this.autor = autor;
        this.nome = nome;
        this.genero = genero;
        this.exemplares = exemplares;
    }

    // Métodos getters e setters para acessar e modificar os atributos privados da classe.

    // Retorna o autor do livro.
    public String getAutor() {
        return autor;
    }

    // Define o autor do livro.
    public void setAutor(String autor) {
        this.autor = autor;
    }

    // Retorna o nome do livro.
    public String getNome() {
        return nome;
    }

    // Define o nome do livro.
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Retorna o gênero do livro.
    public String getGenero() {
        return genero;
    }

    // Define o gênero do livro.
    public void setGenero(String genero) {
        this.genero = genero;
    }

    // Retorna o número de exemplares do livro.
    public int getExemplares() {
        return exemplares;
    }

    // Define o número de exemplares do livro.
    public void setExemplares(int exemplares) {
        this.exemplares = exemplares;
    }

    // Sobrescreve o método toString para fornecer uma representação em string dos atributos do livro.
    @Override
    public String toString() {
        return "Livro{" +
                "autor='" + autor + '\'' +
                ", nome='" + nome + '\'' +
                ", genero='" + genero + '\'' +
                ", exemplares=" + exemplares +
                '}';
    }
}
