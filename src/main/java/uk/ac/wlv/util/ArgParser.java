package uk.ac.wlv.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 参数解析工具类
 */
public class ArgParser {

  public interface Action {
    /**
     * 识别到参数时的动作
     *
     * @param cur  该参数组第一个参数的 index
     * @param args 总参数列表
     * @return 返回值
     */
    public Value apply(int cur, String[] args) throws Exception;
  }

  public static final Action
      SET_TRUE = (cur, args) -> new Value(null, null, null),
      BOY_NEXT_DOOR = (cur, args) -> new Value(null, args[cur + 1], null),
      INT_NEXT_DOOR = (cur, args) -> new Value(Integer.parseInt(args[cur + 1]), null, null);

  private record Arg(int nargs, Action action) {
  }

  public record Value(Integer iVal, String sVal, Boolean bVal) {
  }

  @Getter
  private boolean[] argRecognized;

  private Map<String, Arg> argMap;

  /**
   * 从返回的参数对应表中获取值
   *
   * @param arg      参数名(小写)
   * @param original 原值，若不含此参数则返回原值
   * @param valueMap 参数对应表
   * @return 获取到的值
   */
  public static <T> T extract(String arg, T original, Map<String, Value> valueMap) {
    Value value = valueMap.get(arg);
    if (Objects.isNull(value)) {
      return original;
    }

    if (original instanceof Boolean) {
      return (T) value.bVal;
    } else if (original instanceof Integer) {
      return (T) value.iVal;
    } else if (original instanceof String) {
      return (T) value.sVal;
    } else {
      throw new IllegalArgumentException();
    }
  }

  public ArgParser(boolean[] argRecognized) {
    this.argRecognized = argRecognized;
    this.argMap = new HashMap<>();
  }

  /**
   * 添加需要识别的参数
   *
   * @param name   参数名，必须全小写
   * @param nargs  附带参数数量，最小为 0
   * @param action 识别到参数时的赋值操作
   */
  public void addArgument(String name, int nargs, Action action) {
    argMap.put(name, new Arg(nargs, action));
  }

  public Map<String, Value> parseArgs(String[] args) {
    int n = args.length;
    Map<String, Value> valueMap = new HashMap<>();

    for (int i = 0; i < n; i++) {
      try {
        String key = args[i].toLowerCase();
        Arg arg = argMap.get(key);
        if (Objects.nonNull(arg)) {
          Value val = arg.action.apply(i, args);
          valueMap.put(key, val);

          for (int j = 0; j <= arg.nargs; j++) {
            argRecognized[i + j] = true;
          }

          i += arg.nargs;
        }
      } catch (NumberFormatException e) {
        System.out.println("Error in argument for " + args[i] + ". Integer expected!");
        return null;
      } catch (Exception e) {
        System.out.println("Error in argument for " + args[i] + ". Argument missing?");
        return null;
      }
    }

    return valueMap;
  }
}