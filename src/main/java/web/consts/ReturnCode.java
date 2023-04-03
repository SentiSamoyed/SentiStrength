package web.consts;

public enum ReturnCode {
  SUCCESS(0, "Success");

  private int code;

  private String msg;

  ReturnCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

}
