import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable {
  private ArrayList<ConnectionHandler> connections;
  private ServerSocket serverSocket;
  private boolean socketClose;
  private ExecutorService pool;

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
      pool = Executors.newCachedThreadPool();
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

  class ConnectionHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader messageReader;
    private PrintWriter messageWriter;
    private String nick;

    public ConnectionHandler(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      try {
        createReaderAndWriterForSocket();
        getNickFromUser();
        printlHelp();
        listenMessage();
      } catch (IOException e) {
        shutDown();
      }
    }

    public void sendMessage(String message) {
      messageWriter.println(message);
    }

    public void shutDown() {
      try {
        clientSocket.close();
        messageReader.close();
        messageWriter.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private void createReaderAndWriterForSocket() throws IOException {
      messageWriter = new PrintWriter(clientSocket.getOutputStream(), true);
      messageReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    private void getNickFromUser() throws IOException {
      messageWriter.println("Podaj nick: ");
      nick = messageReader.readLine();
      broadcast(nick + " dołączył do czata");
    }

    private void printlHelp() {
      messageWriter.println("----------- HELP START---------");
      messageWriter.println("|| /quit - opuszczenie czata");
      messageWriter.println("|| /nick nowy_nick - zmiana linku");
      messageWriter.println("----------- HELP STOP---------");
    }

    private void listenMessage() throws IOException {
      String message;
      while ((message = messageReader.readLine()) != null) {
        if (message.startsWith("/nick")) {
          changeNick(message.split(" ", 2)[1]);
        } else if (message.startsWith("/quit")) {
          broadcast(nick + " opuścił konwersację.");
          shutDown();
        } else {
          broadcast(nick + ": " + message);
        }
      }
    }

    private void changeNick(String nick) {
      broadcast(this.nick + " zmienił nick na " + nick);
      this.nick = nick;
      messageWriter.println("Poprawnie zmieniono nick na: " + nick);
    }

    private void broadcast(String message) {
      connections.stream().forEach(connectionHandler -> connectionHandler.sendMessage(message));
    }
  }
}
