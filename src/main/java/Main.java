import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
//         здесь создайте сервер, который отвечал бы на нужные запросы
//         слушать он должен порт 8989
//         отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате
        try (ServerSocket serverSocket = new ServerSocket(8989);) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    // обработка одного подключения
                    String word = in.readLine();
                    BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
                    ObjectMapper mapper = new ObjectMapper();
                    List<PageEntry> pageEntryList = engine.search(word);
                    out.print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageEntryList));
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
