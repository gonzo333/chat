import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable {
  private static ArrayList<ConnectionHandler> connections;
  private ServerSocket serverSocket;
  private boolean socketClose;
  private ExecutorService pool;
  private int usersLimit = 4;


  public ChatServer() {
    connections = new ArrayList<>();
    socketClose = false;
  }

  public static void main(String[] args) {
    ChatServer chatServer = new ChatServer();
    chatServer.run();
  }

  @Override
  public void run() {
    try {
      serverSocket = new ServerSocket(7777);
      pool = Executors.newFixedThreadPool(usersLimit);
      while (!socketClose) {
        Socket clientSocket = serverSocket.accept();
        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
        connections.add(connectionHandler);
        pool.execute(connectionHandler);
      }
    } catch (Exception e) {
      shutDown();
    }
  }

  public void shutDown() {
    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
        socketClose = true;
        connections.stream().forEach(ConnectionHandler::shutDown);
        pool.shutdown();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static ArrayList<ConnectionHandler> getActiveConnectionList(){
    return connections;
  }

  public ServerSocket getServerSocket (){
    return serverSocket;
  }

}
