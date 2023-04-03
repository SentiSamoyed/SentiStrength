package uk.ac.wlv.util;

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
    Value apply(int cur, String[] args) throws Exception;
  }

  public static final Action
      SET_TRUE = (cur, args) -> new Value(true),
      SET_FALSE = (cur, args) -> new Value(false),
      BOY_NEXT_DOOR = (cur, args) -> new Value(args[cur + 1]),
      INT_NEXT_DOOR = (cur, args) -> new Value(Integer.parseInt(args[cur + 1])),
      DOUBLE_NEXT_DOOR = (cur, args) -> new Value(Double.parseDouble(args[cur + 1]));
  ;

  private record Arg(int nargs, Action action) {
  }

  public record Value(Integer iVal, String sVal, Boolean bVal, Double dVal) {
    public Value(Integer iVal, String sVal, Boolean bVal, Double dVal) {
      this.iVal = iVal;
      this.sVal = sVal;
      this.bVal = bVal;
      this.dVal = dVal;
    }

    public Value(Integer iVal) {
      this(iVal, null, null, null);
    }

    public Value(String sVal) {
      this(null, sVal, null, null);
    }

    public Value(Boolean bVal) {
      this(null, null, bVal, null);
    }

    public Value(Double dVal) {
      this(null, null, null, dVal);
    }
  }

  final private boolean[] argRecognized;
  final private Map<String, Arg> argMap;
  final private Map<String, Value> valueMap;

  /**
   * 从返回的参数对应表中获取值
   *
   * @param arg      参数名(小写)
   * @param original 原值，若不含此参数则返回原值
   * @return 获取到的值
   */
  public <T> T extract(String arg, T original) {
    Value value = valueMap.get(arg.toLowerCase());
    if (Objects.isNull(value)) {
      return original;
    }

    if (original instanceof Boolean) {
      return (T) value.bVal;
    } else if (original instanceof Integer) {
      return (T) value.iVal;
    } else if (original instanceof String) {
      return (T) value.sVal;
    } else if (original instanceof Double) {
      return (T) value.dVal;
    } else if (original instanceof Float) {
      return (T) Float.valueOf(value.dVal.floatValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public ArgParser(boolean[] argRecognized) {
    this.argRecognized = argRecognized;
    this.argMap = new HashMap<>();
    this.valueMap = new HashMap<>();
  }

  /**
   * 添加需要识别的参数
   *
   * @param name   参数名，必须全小写
   * @param nargs  附带参数数量，最小为 0
   * @param action 识别到参数时的赋值操作
   */
  public void addArgument(String name, int nargs, Action action) {
    argMap.put(name.toLowerCase(), new Arg(nargs, action));
  }

  public boolean parseArgs(String[] args) {
    int n = args.length;

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
        return false;
      } catch (Exception e) {
        System.out.println("Error in argument for " + args[i] + ". Argument missing?");
        return false;
      }
    }

    return true;
  }

  /**
   * 是否识别到参数 key
   *
   * @param key 参数名
   * @return 是否识别到
   */
  public boolean containsArg(String key) {
    return valueMap.containsKey(key.toLowerCase());
  }
}
