package kuzmin.http_server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AddServlet extends HttpServlet {
    @Override
    public void destroy() {
        System.out.println("Add servlet destroy");
    }

    @Override
    public void init() throws ServletException {
        System.out.println("Add servlet created");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {//метод занимается обработкой запросов
        System.out.println("kuzmin.http_server.Request go to Simple servlet");
        PrintWriter out = resp.getWriter();//запрашиваем принтврайтер у обьекта ответа (данные которые будут лететь в сторону клиента)
        int a = Integer.parseInt(req.getParameter("a"));
        int b = Integer.parseInt(req.getParameter("b"));
        int result = a + b;

        out.println("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + 200 + "\r\n" +
                "\r\n"  +
                "<h1>result</h1>");


        out.flush();
        out.close();
    }
}
