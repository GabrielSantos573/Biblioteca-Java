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
                System.out.println("Escolha uma opção: 1-LISTAR, 2-CADASTRAR, 3-ALUGAR, 4-DEVOLVER, 5-SAIR");
                String comando = scanner.nextLine();

                if ("5".equals(comando)) {
                    output.writeObject(comando);
                    output.flush();
                    break;
                }

                output.writeObject(comando);
                output.flush();  // Ensure all data is sent before reading response

                switch (comando) {
                    case "1":
                        @SuppressWarnings("unchecked")
                        List<Livro> livros = (List<Livro>) input.readObject();
                        for (Livro livro : livros) {
                            System.out.println(livro);
                        }
                        break;
                    case "2":
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
                        output.flush();
                        System.out.println(input.readObject()); // Mensagem de confirmação
                        break;
                    case "3":
                        System.out.print("Nome do livro para alugar: ");
                        String tituloAlugar = scanner.nextLine();
                        output.writeObject(tituloAlugar);
                        output.flush();
                        System.out.println(input.readObject());
                        break;
                    case "4":
                        System.out.print("Nome do livro para devolver: ");
                        String tituloDevolver = scanner.nextLine();
                        output.writeObject(tituloDevolver);
                        output.flush();
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
