package kuzmin.http_server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Response implements HttpServletResponse {
    //System.getProperty("user.dir") : Директрия из которой произвдится работа
    //File.separator : системный разделитель
    //"webroot" : папка
    //это все выше описанное путь к папке webroot где бы она нележала, где бы не запустили приложение там в этой папке и будем искать

    private static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    Request request;
    OutputStream outputStream;
    PrintWriter printWriter;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.printWriter = new PrintWriter(outputStream);
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() {
        try {
            File file = new File(WEB_ROOT, request.getRequestURI()); //отдаем КОНКРЕТНЫЙ файл указывая где он (WEB_ROOT)
            //и имя его берем из обьекта запроса точнее из URI который указал клиент
            if (!file.exists()) {//если такого файла нет
                String errorMessage =
                        "HTTP/1.1 404 File Not Found\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: 23\r\n" +
                                "\r\n" +
                                "<h1>File Not Found</h1>";
                outputStream.write(errorMessage.getBytes());
                return;
            }

            Path path = Path.of(WEB_ROOT + request.getRequestURI());

            final long length = Files.size(path);
            outputStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            //"Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(path, outputStream);

//            try (FileInputStream fileInputStream = new FileInputStream(file)) {
//                fileInputStream.transferTo(outputStream);
//            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    //ниже идут методы HttpServletResponse который можно заполнить логикой но мы реализовали getWriter()
    // =====default=====


    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return "";
    }

    @Override
    public String encodeRedirectURL(String s) {
        return "";
    }

    @Override
    public String encodeUrl(String s) {
        return "";
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return "";
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    @Override
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return "";
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return List.of();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return List.of();
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }


    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
