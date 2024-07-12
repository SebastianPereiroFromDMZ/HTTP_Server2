package kuzmin.http_server;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor {  //задача процессора (этого класса) получить входные данные,
    //запрос ответ и по какойто логике ох обработать (конкретно этот класс будет работать с динамическими ресурсами)

    //http://localhost:8189/servlet/SimpleServlet примерно такой будет приходить запрос, задача вытащить из этого пути kuzmin.http_server.SimpleServlet

    public void process(Request request, Response response) {
        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf('/') + 1);//вытащили kuzmin.http_server.SimpleServlet (имя сервлета)
        URLClassLoader loader = null;//таперь необходимо повозиться с рефлексией, создаем класслоадер и с помощью его затащить класс (kuzmin.http_server.SimpleServlet) в память
        try {
            //вначале URLClassLoader указываем где ему искать классы по каким адресам
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            //путь где будут лежать исхдники с сервлетами которые необходимо будет загрузить класслоадеру
            String repository = new URL(
                    "file",
                    null,
                    (new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getCanonicalPath() + File.separator).toString()
            ).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //теперь поскольку указали класслоадеру где могут быть файлы с сервлетами, и теперь возможно по имени сервлета загружать указанный файл в память,
        //по нему создавать обьекты а у них вызывать какие то методы

        Class myClass = null;

        try {
            //собираем полное имя сервлета по которому будет производится загрузка
           // final String jjj =

            final String fullClassName = this.getClass().getPackage().getName() + "." + servletName;//здесь по идее должен
            //выстраиваться путь до класса который лоадер должен загрузить, но почему то за место this.getClass().getPackage().getName() выхдит пустая строка
            myClass = loader.loadClass(fullClassName);//грузим класс
            //выше мы загрузили класс в память и с помощью рефлексии (с помощью ссылки на класс)
            //можем попросить у класса конструктор и через конструктор создать обьект, даже того класса с которым не имеем дела
            Servlet servlet = (Servlet) myClass.getDeclaredConstructor().newInstance();
            servlet.service(request, response);//в созданный рефлексией сервлету вызывам его рабочий метод service
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}