package kuzmin.http_server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

    private int port;
    private boolean shutdown = false;

    public HTTPServer(int port) {
        this.port = port;
    }

    public void await() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен на порту: " + port);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!shutdown) {
            try (
                    Socket socket = serverSocket.accept();//блокирующая операция (садимся и ждем пока кто нибудь подключится))
                    InputStream inputStream = socket.getInputStream();//байтовый входящий пток
                    OutputStream outputStream = socket.getOutputStream();//байтовый выходящий поток
            ) {
                System.out.println("Получен входящий запрос");

                Request request = new Request(inputStream);//создаем обьект запроса, и отдаем ему в конструктор входной поток

                request.parse();//парсим его

                System.out.println("Адрес запроса: " + request.getRequestURI());

                Response response = new Response(outputStream);//создаем обькт ответа
                response.setRequest(request);//передаем обьект запроса в обьект ответа

                if (request.getRequestURI().startsWith("/servlet/")){//если запрос идет на динамический ресурс (на сервлет)
                    ServletProcessor servletProcessor = new ServletProcessor();
                    servletProcessor.process(request, response);

                }else {//иначе идет запрос на статический ресурс
                    StaticResourceProcessor staticResourceProcessor = new StaticResourceProcessor();//создаем процессор
                    staticResourceProcessor.process(request, response);//и отдадим ему обьекты для работы
                }

                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        HTTPServer httpServer = new HTTPServer(8189);
        httpServer.await();
    }
}
