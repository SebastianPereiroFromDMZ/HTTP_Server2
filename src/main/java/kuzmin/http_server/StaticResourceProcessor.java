package kuzmin.http_server;

public class StaticResourceProcessor {  //задача процессора (этого класса) получить входные данные,
    //запрос ответ и по какойто логике ох обработать (конкретно этот класс будет работать со статическими ресурсами)

    public void process(Request request, Response response) {
        response.sendStaticResource();//вызываем ответ к клиенту

    }


}
