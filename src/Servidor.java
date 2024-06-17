import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Servidor {
    private static final int PORT = 12345;
    private static List<Livro> livros;
    private static final String FILE_PATH = "livros.json";

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            carregarLivros();

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClienteHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void carregarLivros() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Gson gson = new Gson();
            Map<String, List<Livro>> map = gson.fromJson(reader, new TypeToken<Map<String, List<Livro>>>() {}.getType());
            livros = map.get("livros");
        } catch (IOException e) {
            e.printStackTrace();
            livros = new ArrayList<>();
        }
    }

    private static void salvarLivros() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Gson gson = new Gson();
            Map<String, List<Livro>> map = Map.of("livros", livros);
            gson.toJson(map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClienteHandler implements Runnable {
        private Socket socket;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())
            ) {
                while (true) {
                    String comando = (String) input.readObject();
                    if (comando == null || comando.equals("5")) {
                        break;
                    }
                    switch (comando) {
                        case "1":
                            carregarLivros();  // Carrega os livros do arquivo
                            output.writeObject(livros);
                            output.flush();
                            break;
                        case "2":
                            Livro novoLivro = (Livro) input.readObject();
                            livros.add(novoLivro);
                            salvarLivros();
                            output.writeObject("Livro cadastrado com sucesso!");
                            output.flush();
                            break;
                        case "3":
                            String tituloAlugar = (String) input.readObject();
                            boolean alugado = false;
                            System.out.println("Tentando alugar: " + tituloAlugar); // Debug
                            for (Livro livro : livros) {
                                System.out.println(livro.getNome() + " - Exemplares: " + livro.getExemplares()); // Debug
                                if (livro.getNome().equalsIgnoreCase(tituloAlugar)) {
                                    if (livro.getExemplares() > 0) {
                                        livro.setExemplares(livro.getExemplares() - 1);
                                        salvarLivros(); // Salva a lista de livros atualizada no arquivo
                                        carregarLivros(); // Recarrega a lista de livros do arquivo para garantir a sincronização
                                        output.writeObject("Livro alugado com sucesso!");
                                        alugado = true;
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
                            String tituloDevolver = (String) input.readObject();
                            boolean devolvido = false;
                            for (Livro livro : livros) {
                                if (livro.getNome().equalsIgnoreCase(tituloDevolver)) {
                                    livro.setExemplares(livro.getExemplares() + 1);
                                    salvarLivros();
                                    carregarLivros(); // Recarrega a lista de livros do arquivo para garantir a sincronização
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
                            output.writeObject("Comando não reconhecido.");
                            output.flush();
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
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
