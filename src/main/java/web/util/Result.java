package web.util;

import lombok.Data;
import web.enums.ReturnCode;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 返回封装结构体
 */
@Data
public class Result<T> {
  /**
   * 返回码
   */
  private int code;
  /**
   * 返回信息
   */
  private String msg;
  /**
   * 返回数据本身
   */
  private T data;

  public Result(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public Result(ReturnCode rc, T data) {
    this(rc.getCode(), rc.getMsg(), data);
  }

  public static <T> Result<T> buildSuccess(T data) {
    return new Result<>(ReturnCode.SUCCESS, data);
  }
}
