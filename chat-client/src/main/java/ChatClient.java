import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient implements Runnable {
  private Socket clientSocket;
  private BufferedReader messageIn;
  private PrintWriter messageOut;
  private boolean socketClose;

  public static void main(String[] args) {
    ChatClient chatClient = new ChatClient();
    chatClient.run();
  }

  @Override
  public void run() {

    try {
      clientSocket = new Socket("127.0.0.1", 7777);
      messageOut = new PrintWriter(clientSocket.getOutputStream(), true);
      messageIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      InputHandler inputHandler = new InputHandler();
      Thread thread = new Thread(inputHandler);
      thread.start();

      String inMessage;
      while ((inMessage = messageIn.readLine()) != null) {
        System.out.println(inMessage);
      }
    } catch (IOException e) {
      shutDown();
    }
  }

  public void shutDown() {
    socketClose = true;
    try {
      messageIn.close();
      messageOut.close();
      if (!clientSocket.isClosed()) {
        clientSocket.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  class InputHandler implements Runnable {
    @Override
    public void run() {
      try {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while (!socketClose) {
          String message = inputReader.readLine();
          messageOut.println(message);
          if (message.equals("/quit")) {
            messageIn.close();
            shutDown();
          }
        }
      } catch (IOException e) {
        shutDown();
      }
    }
  }
}
