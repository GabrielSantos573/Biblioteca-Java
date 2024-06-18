import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Servidor {
    // Porta onde o servidor irá escutar por conexões
    private static final int PORT = 12345;
    // Lista de livros armazenados
    private static List<Livro> livros;
    // Caminho do arquivo JSON que armazena os livros
    private static final String FILE_PATH = "livros.json";

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            // Cria um ServerSocket para escutar na porta especificada
            serverSocket = new ServerSocket(PORT);
            // Carrega a lista de livros do arquivo JSON
            carregarLivros();

            // Loop infinito para aceitar conexões de clientes
            while (true) {
                // Aceita uma nova conexão
                Socket socket = serverSocket.accept();
                // Cria uma nova thread para lidar com o cliente conectado
                new Thread(new ClienteHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Fecha o ServerSocket se ele não estiver fechado
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Método para carregar a lista de livros do arquivo JSON
    private static void carregarLivros() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Gson gson = new Gson();
            // Deserializa o JSON para um mapa contendo uma lista de livros
            Map<String, List<Livro>> map = gson.fromJson(reader, new TypeToken<Map<String, List<Livro>>>() {}.getType());
            livros = map.get("livros");
        } catch (IOException e) {
            e.printStackTrace();
            livros = new ArrayList<>();
        }
    }

    // Método para salvar a lista de livros no arquivo JSON
    private static void salvarLivros() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Cria um mapa contendo a lista de livros
            Map<String, List<Livro>> map = Map.of("livros", livros);
            // Serializa o mapa para JSON e escreve no arquivo
            gson.toJson(map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe interna que lida com a conexão com o cliente
    private static class ClienteHandler implements Runnable {
        private Socket socket;

        // Construtor que recebe o socket do cliente
        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                // Streams para comunicação com o cliente
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())
            ) {
                while (true) {
                    // Lê o comando enviado pelo cliente
                    String comando = (String) input.readObject();
                    if (comando == null || comando.equals("5")) {
                        break;
                    }
                    // Processa o comando recebido
                    switch (comando) {
                        case "1":
                            // Carrega a lista de livros do arquivo
                            carregarLivros();
                            // Envia a lista de livros para o cliente
                            output.writeObject(livros);
                            output.flush();
                            break;
                        case "2":
                            // Lê o novo livro enviado pelo cliente
                            Livro novoLivro = (Livro) input.readObject();
                            // Adiciona o novo livro à lista
                            livros.add(novoLivro);
                            // Salva a lista de livros atualizada no arquivo
                            salvarLivros();
                            // Envia uma mensagem de confirmação para o cliente
                            output.writeObject("Livro cadastrado com sucesso!");
                            output.flush();
                            break;
                        case "3":
                            // Lê o título do livro a ser alugado enviado pelo cliente
                            String tituloAlugar = (String) input.readObject();
                            boolean alugado = false;
                            System.out.println("\nTentando alugar: " + tituloAlugar); // Debug
                            for (Livro livro : livros) {
                                if (livro.getNome().equalsIgnoreCase(tituloAlugar)) {
                                    if (livro.getExemplares() > 0) {
                                        // Diminui o número de exemplares do livro
                                        livro.setExemplares(livro.getExemplares() - 1);
                                        // Salva a lista de livros atualizada no arquivo
                                        salvarLivros();
                                        // Envia uma mensagem de confirmação para o cliente
                                        output.writeObject("Livro alugado com sucesso!");
                                        alugado = true;
                                        System.out.println(livro.getNome() + " - Exemplares: " + livro.getExemplares()); // Debug
                                        break;
                                    } else {
                                        output.writeObject("Não há exemplares disponíveis para aluguel.");
                                        alugado = true;
                                        break;
                                    }
                                }
                            }
                            if (!alugado) {
                                output.writeObject("Livro não disponível para aluguel.");
                            }
                            output.flush();
                            break;
                        case "4":
                            // Lê o título do livro a ser devolvido enviado pelo cliente
                            String tituloDevolver = (String) input.readObject();
                            boolean devolvido = false;
                            for (Livro livro : livros) {
                                if (livro.getNome().equalsIgnoreCase(tituloDevolver)) {
                                    // Aumenta o número de exemplares do livro
                                    livro.setExemplares(livro.getExemplares() + 1);
                                    // Salva a lista de livros atualizada no arquivo
                                    salvarLivros();
                                    // Envia uma mensagem de confirmação para o cliente
                                    output.writeObject("Livro devolvido com sucesso!");
                                    devolvido = true;
                                    break;
                                }
                            }
                            if (!devolvido) {
                                output.writeObject("Livro não encontrado.");
                            }
                            output.flush();
                            break;
                        default:
                            // Envia uma mensagem indicando que o comando não foi reconhecido
                            output.writeObject("Comando não reconhecido.");
                            output.flush();
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                // Fecha o socket do cliente
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
