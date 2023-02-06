import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer implements Runnable {
  public static final String GENERAL_ROOM_NAME = "GENERAL";
  private Collection<ChatUser> chatUsers;
  private Collection<ChatRoom> chatRooms;
  private ServerSocket serverSocket;
  private boolean socketClose;
  private ExecutorService pool;
  private int usersLimit = 4;

  public ChatServer() {
    chatUsers = new ArrayList<>();
    chatRooms = new ArrayList<>();
    chatRooms.add(new ChatRoom(GENERAL_ROOM_NAME, null, new ArrayList<>()));
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
        ChatUser chatUser = new ChatUser(clientSocket, this);
        chatUsers.add(chatUser);
        pool.execute(chatUser);
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
        chatUsers.stream().forEach(ChatUser::destroyUser);
        pool.shutdown();
      } catch (IOException e) {
        throw new RuntimeException("Exception during shut down");
      }
    }
  }

  public void addChatUserToChatRoomByName(String chatRoomName, ChatUser chatUser) {
    Optional <ChatRoom> userChatRoom = chatRooms.stream()
        .filter(chatRoom -> chatRoom.getName().equals(chatRoomName))
        .findFirst();
    if (userChatRoom.isPresent()) {
      userChatRoom.get().addChatUser(chatUser);
    }
  }

  public void createChatRoom(String roomName, ChatUser creator) {
    Collection<ChatUser> users = new ArrayList<>();
    users.add(creator);
    chatRooms.add(new ChatRoom(roomName, creator, users));
  }

  public Optional<ChatRoom> getUserRoom(ChatUser chatUser){
    return chatRooms.stream()
            .filter(chatRoom -> chatRoom.getChatUsers()
                    .stream()
                    .anyMatch(chatRoomUser -> chatRoomUser.equals(chatUser)))
            .findFirst();
  }
}
