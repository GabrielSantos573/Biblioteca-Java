import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Cliente {
    // Endereço do servidor e porta que será utilizada para a conexão
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Usando try-with-resources para garantir que os recursos sejam fechados automaticamente
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)
        ) {
            while (true) {
                // Exibe o menu de opções para o usuário
                System.out.println("Escolha uma opção: 1-LISTAR, 2-CADASTRAR, 3-ALUGAR, 4-DEVOLVER, 5-SAIR");
                String comando = scanner.nextLine();

                // Se a opção escolhida for "5", envia o comando para o servidor e encerra o loop
                if ("5".equals(comando)) {
                    output.writeObject(comando);
                    output.flush();
                    break;
                }

                // Envia o comando escolhido pelo usuário para o servidor
                output.writeObject(comando);
                output.flush();  // Garante que todos os dados sejam enviados antes de ler a resposta

                switch (comando) {
                    case "1":
                        // Recebe a lista de livros do servidor e exibe
                        @SuppressWarnings("unchecked")
                        List<Livro> livros = (List<Livro>) input.readObject();
                        for (Livro livro : livros) {
                            System.out.println(livro);
                        }
                        break;
                    case "2":
                        // Solicita as informações do novo livro ao usuário
                        System.out.print("Autor: ");
                        String autor = scanner.nextLine();
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("Gênero: ");
                        String genero = scanner.nextLine();
                        System.out.print("Número de exemplares: ");
                        int exemplares = Integer.parseInt(scanner.nextLine());

                        // Cria um novo objeto Livro com as informações fornecidas
                        Livro novoLivro = new Livro(autor, nome, genero, exemplares);
                        // Envia o novo livro para o servidor
                        output.writeObject(novoLivro);
                        output.flush();
                        // Exibe a mensagem de confirmação recebida do servidor
                        System.out.println(input.readObject());
                        break;
                    case "3":
                        // Solicita o nome do livro a ser alugado
                        System.out.print("Nome do livro para alugar: ");
                        String tituloAlugar = scanner.nextLine();
                        // Envia o nome do livro para o servidor
                        output.writeObject(tituloAlugar);
                        output.flush();
                        // Exibe a resposta do servidor
                        System.out.println(input.readObject());
                        break;
                    case "4":
                        // Solicita o nome do livro a ser devolvido
                        System.out.print("Nome do livro para devolver: ");
                        String tituloDevolver = scanner.nextLine();
                        // Envia o nome do livro para o servidor
                        output.writeObject(tituloDevolver);
                        output.flush();
                        // Exibe a resposta do servidor
                        System.out.println(input.readObject());
                        break;
                    default:
                        // Informa ao usuário que o comando não foi reconhecido
                        System.out.println("Comando não reconhecido.");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Trata exceções de entrada/saída e de classe não encontrada
            e.printStackTrace();
        }
    }
}
