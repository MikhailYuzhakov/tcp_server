import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class tcp_server {

	static ExecutorService executeIt = Executors.newFixedThreadPool(2);

	public static void main(String[] args) throws SocketException {
		
		// стартуем сервер на порту 3345 и инициализируем переменную для обработки консольных команд с самого сервера
		try (ServerSocket server = new ServerSocket(3345)) {
			System.out.println("Server 2.0 socket " + server.getLocalSocketAddress() +  " created");

			// стартуем цикл при условии что серверный сокет не закрыт
			while (!server.isClosed()) {			
				Socket client = server.accept(); //ожидание установки соеднинения с клиентом
				executeIt.execute(new MonoThreadClientHandler(client)); //передаем в отдельную нить общение с клиентом
				System.out.print("Connection with " + client.getRemoteSocketAddress() + " accepted.");
			}
			executeIt.shutdown(); //как только все нити отработали, закрываем пул нитей
		} catch (SocketException e) {
			System.out.println("Client close connection.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

