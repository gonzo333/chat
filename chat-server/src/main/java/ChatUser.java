import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

class ChatUser implements Runnable {
  private Socket clientSocket;
  private ChatServer chatServer;
  private BufferedReader messageReader;
  private PrintWriter messageWriter;
  private String nick;

  public ChatUser(Socket clientSocket, ChatServer chatServer) {
    this.clientSocket = clientSocket;
    this.chatServer = chatServer;
  }

  @Override
  public void run() {
    try {
      createReaderAndWriter();
      getNickFromUser();
      choiseRoomOption();
      printlHelp();
      listenMessage();
    } catch (IOException e) {
      destroyUser();
    }
  }

  public void destroyUser() {
    try {
      clientSocket.close();
      messageReader.close();
      messageWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createReaderAndWriter() throws IOException {
    messageWriter = new PrintWriter(clientSocket.getOutputStream(), true);
    messageReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  private void getNickFromUser() throws IOException {
    messageWriter.println("Podaj nick: ");
    nick = messageReader.readLine();
  }

  private void choiseRoomOption() throws IOException {
    displayRoomOptions();

    switch (messageReader.readLine()) {
      case Constants.CHAT_ROOM_CHOISE_GENERAL_ROOM -> {
        chatServer.addChatUserToChatRoomByName(ChatServer.GENERAL_ROOM_NAME, this);
        messageWriter.println("Czatujesz na kanale ogolnym");
      }
      case Constants.CHAT_ROOM_CHOISE_CREATE_ROOM -> {
        messageWriter.println("Podaj nazwe pokoju i zatwierdz enter, aby stworzyc pokoj");
        chatServer.createChatRoom(messageReader.readLine(), this);
        messageWriter.println("Gratulacje stworzyles pokoj");
      }
      case Constants.CHAT_ROOM_CHOISE_JOIN_ROOM -> {
        messageWriter.println("Podaj nazwe pokoju do ktorego chcesz dolaczyc");
        chatServer.addChatUserToChatRoomByName(messageReader.readLine(), this);
        messageWriter.println("Poprawnie dolaczyles do pokoju");
      }
      default -> {
        messageWriter.println("Niezana opcja");
        choiseRoomOption();
      }
    }
  }

  private void displayRoomOptions() {
    messageWriter.println("Wybierz opcję: ");
    messageWriter.println("1 - dołącz do ogólnego pokoju");
    messageWriter.println("2 - stwórz własny pokój");
    messageWriter.println("3 - dołącz do istniejącego pokoju");
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
        sendMessage(nick + " opuścił konwersację.");
        destroyUser();
      } else {
        sendMessage(nick + ": " + message);
      }
    }
  }

  private void changeNick(String newNick) {
    sendMessage(nick + " zmienił nick na " + newNick);
    nick = newNick;
    messageWriter.println("Poprawnie zmieniono nick na: " + newNick);
  }

  private void sendMessage(String message) {
    Optional<ChatRoom> userChatRoom = chatServer.getUserRoom(this);
    if (userChatRoom.isPresent()) {
      userChatRoom.get().sendMessageToRoom(message, this);
    }
  }

  public void displayMessage(String message) {
    messageWriter.println(message);
  }
}
