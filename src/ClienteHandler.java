import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ClienteHandler implements Runnable {
    private Socket socket;
    private List<Livro> livros;
    private String filePath;

    public ClienteHandler(Socket socket, List<Livro> livros, String filePath) {
        this.socket = socket;
        this.livros = livros;
        this.filePath = filePath;
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
                        carregarLivros();
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
                        System.out.println("\nTentando alugar: " + tituloAlugar);
                        for (Livro livro : livros) {
                            if (livro.getNome().equalsIgnoreCase(tituloAlugar)) {
                                if (livro.getExemplares() > 0) {
                                    livro.setExemplares(livro.getExemplares() - 1);
                                    salvarLivros();
                                    output.writeObject("Livro alugado com sucesso!");
                                    alugado = true;
                                    System.out.println(livro.getNome() + " - Exemplares: " + livro.getExemplares());
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

    private void carregarLivros() {
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Map<String, List<Livro>> map = gson.fromJson(reader, new TypeToken<Map<String, List<Livro>>>() {}.getType());
            livros = map.get("livros");
        } catch (IOException e) {
            e.printStackTrace();
            livros = new ArrayList<>();
        }
    }

    private void salvarLivros() {
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, List<Livro>> map = Map.of("livros", livros);
            gson.toJson(map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
