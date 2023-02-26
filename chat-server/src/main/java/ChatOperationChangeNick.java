public class ChatOperationChangeNick implements ChatMessageOperation {
  @Override
  public void makeOperation(ChatUser chatUser, String message) {
    chatUser.changeNick(message.split(" ", 2)[1]);
  }
}
