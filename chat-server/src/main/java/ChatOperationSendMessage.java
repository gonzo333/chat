public class ChatOperationSendMessage implements ChatMessageOperation {
  @Override
  public void makeOperation(ChatUser chatUser, String message) {
    chatUser.sendMessage(chatUser.getNick() + ": " + message);
  }
}
