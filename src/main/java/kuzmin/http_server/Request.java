package kuzmin.http_server;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public class Request implements HttpServletRequest {

    private InputStream input;
    private String uri;
    private Map<String, String> parameters;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse() {//метод расшифровки запрса

        try {

            StringBuilder stringBuilder = new StringBuilder(4096);//укажем что размер билдера будет равен размеру буфера

            int n;

            byte[] buffer = new byte[4096];//создаем буфер

            try {
                n = input.read(buffer);//записываем в буфер все что пришло в запросе, метод .read() возвращает длинну входящего сообщения,
                //и эту длинну присваиваем в переменную n типа int, такой способ необходим для чистоты:
                //тоесть мы будем в строку (String message) присваивать не весь буфер а только то что пришло в запросе,
                //например нам пришло 20 byte и чтобы не записывать в String message весь буфер (byte[4096]) мы запишем только те 20 byte котрые пришли

                //можно использовать код ниже, но в данном примере будем использовать стринг билдер!!!
                //String message = new String(buffer, 0, n);//собираем строку из буфера начиная с 0 позиции и до длинны которую вычислил метод .read()
                //(все что нам пришло в запросе)
            } catch (IOException e) {
                e.printStackTrace();
                n = -1;//если входных данных нет, и ниже цикл уже не бует исполняться
            }

            for (int i = 0; i < n; i++) {
                stringBuilder.append((char) buffer[i]);//собираем стриг билдер из буффреа (переводя байты в символы)
            }

            String requestString = stringBuilder.toString();//получаем строку запроса

            uri = parseUri(requestString);//достаем адрес из запроса (на какой эндпоинт пришол запрос)

            parameters = parseParameters(requestString);//достаем параметры запроса

            System.out.println(requestString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseUri(String requestString) {//вытаскиваем адрес запроса (на какой адрес был выполнен запрос)
        int index1, index2;//создаем 2 индекса
        index1 = requestString.indexOf(' ');//первый индекс смотрим где находится первый пробел
        //(Пример: вот строка запроса : "GET / HTTP/1.1" сначала идет метод запроса, потом пробел,
        //потом на какой адрес был сделан запрос, в этом случае на корень (/) далее пробел и наконец версия HTTP)
        if (index1 != -1) {//если нашли пробел
            index2 = requestString.indexOf('?', index1 + 1);//сначала делаем проверку на то есть ли в строке запроса параметры например:
            //GET /servlet/SimpleServlet?abc=10 HTTP/1.1 здесь сначала идет название метода, потом пробел,
            //потом путь на который был сделан запрос, потом пробел, потом протокол.
            //И здесь проводим проверку на наличие символа вопроса в строке запроса если он есть то запрос был сделан с параметрами,
            //значит делаем индекс до того момента кгда был найден символ вопроса
            if (index2 > index1){//если найден символ вопроса индекс2 будет больше индекса1
                return requestString.substring(index1 + 1, index2);//вырезаем подстроку из строки запроса по индексам
            }
            index2 = requestString.indexOf(' ', index1 + 1);//ищем второй пробел начиная с первого символа после первого пробела
            if (index2 > index1) {//если второй пробел нашли (а он обязательно будет с большим индексм нежели первый)
                return requestString.substring(index1 + 1, index2);//то вернем из входящей строки подстроку от индекса 1 до индекса 2 тоесть АДРЕС (эндпоинт)
            }
        }
        return null;
    }

    //параметры живут в uri строке запроса например: /add?a=10&b=hello ... (здесь мы будем работать только с GET запросм где параметры идут в строке запроса,
    //POST запрос там параметры уже идут в теле запроса)
    private Map<String, String> parseParameters(String requestString) {
        Map<String, String> out = new HashMap<>();//готовим мапу которую вернем (список параметров)
        int index1 = requestString.indexOf('?');//ищем вопросик после которого идут параметры
        if (index1 == -1) {//если знакавопроса нет в строке
            return Collections.emptyMap();//возвращаем пустую мапу
        }
        int index2 = requestString.indexOf(' ', index1 + 1);//ищем второй индекс пробел, это конец параметров, поле параметров в строке идет пробел
        if (index2 == -1) {//если почемуто мы не нашли пробел
            return Collections.emptyMap();//по хорошему это эксепшн, но тут вернем просто пустую мапу
        }

        Arrays.stream(requestString.substring(index1 + 1, index2).split("&")) //берем входную строку и делаем стрим
                // (стрим работаем с структурами данными (множество обьектов) поэтому в пераметрах стрима мы делаем массив строк из входной строки)
                //выпиливаем из строки запроса строку с параметрами a=10&b=hello разделяем по '&' на две строки a=10 и b=hello (массив строк)
                .map(param -> param.split("=")) //разбиваем получившиеся строки на 2 части: a и 10 (вот и получилось ключь, значение)
                .forEach(keyValue -> out.put(keyValue[0], keyValue[1]));//собираем в мапу
        return Collections.unmodifiableMap(out); //возвращаем мапу которую нельзя изменить
    }

//    public String getUri() {
//        return uri;
//    }

    @Override
    public String getRequestURI() {
        return uri;
    }

    @Override
    public String getParameter(String s) {//дстаем параметр по ключу
        return parameters.get(s);
    }

    //ниже идут методы HttpServletRequest который можно заполнить логикой но мы реализовали getRequestURI()
    // =====default======

    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return "";
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public String getMethod() {
        return "";
    }

    @Override
    public String getPathInfo() {
        return "";
    }

    @Override
    public String getPathTranslated() {
        return "";
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getQueryString() {
        return "";
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return "";
    }


    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return "";
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return "";
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return List.of();
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }


    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Map.of();
    }

    @Override
    public String getProtocol() {
        return "";
    }

    @Override
    public String getScheme() {
        return "";
    }

    @Override
    public String getServerName() {
        return "";
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return "";
    }

    @Override
    public String getRemoteHost() {
        return "";
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return "";
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "";
    }

    @Override
    public String getLocalAddr() {
        return "";
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
