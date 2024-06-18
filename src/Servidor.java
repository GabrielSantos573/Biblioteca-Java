//java -cp "lib/gson-2.11.0.jar;bin" Servidor

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Servidor {
    private static final int PORT = 12345;
    private static List<Livro> livros = new ArrayList<>();
    private static final String FILE_PATH = "livros.json";

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            carregarLivros();

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClienteHandler(socket, livros, FILE_PATH)).start();
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
}
