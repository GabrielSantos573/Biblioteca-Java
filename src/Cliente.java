import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Cliente {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)
        ) {
            while (true) {
                System.out.println("Escolha uma opção: LISTAR, CADASTRAR, ALUGAR, DEVOLVER, SAIR");
                String comando = scanner.nextLine().toUpperCase();
                output.writeObject(comando);

                if ("SAIR".equals(comando)) {
                    break;
                }

                switch (comando) {
                    case "LISTAR":
                        List<Livro> livros = (List<Livro>) input.readObject();
                        for (Livro livro : livros) {
                            System.out.println(livro);
                        }
                        break;
                    case "CADASTRAR":
                        System.out.print("Autor: ");
                        String autor = scanner.nextLine();
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("Gênero: ");
                        String genero = scanner.nextLine();
                        System.out.print("Número de exemplares: ");
                        int exemplares = Integer.parseInt(scanner.nextLine());

                        Livro novoLivro = new Livro(autor, nome, genero, exemplares);
                        output.writeObject(novoLivro);
                        System.out.println(input.readObject());
                        break;
                    case "ALUGAR":
                        System.out.print("Nome do livro para alugar: ");
                        String tituloAlugar = scanner.nextLine();
                        output.writeObject(tituloAlugar);
                        System.out.println(input.readObject());
                        break;
                    case "DEVOLVER":
                        System.out.print("Nome do livro para devolver: ");
                        String tituloDevolver = scanner.nextLine();
                        output.writeObject(tituloDevolver);
                        System.out.println(input.readObject());
                        break;
                    default:
                        System.out.println("Comando não reconhecido.");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
