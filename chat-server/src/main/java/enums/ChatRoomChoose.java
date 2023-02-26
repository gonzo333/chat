package enums;

public enum ChatRoomChoose {
  GENERAL("1"),
  CREATE("2"),
  JOIN("3");

  public final String label;

  ChatRoomChoose(String label) {
    this.label = label;
  }
}
