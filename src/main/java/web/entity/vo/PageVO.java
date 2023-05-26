package web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author tanziyue
 * @date 2023/5/23
 * @description 分页结构体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageVO<T> {

  /**
   * 内容列表
   */
  private List<T> contentList;
  /**
   * 页数
   */
  private Integer pageCount;
  /**
   * 页大小
   */
  private Integer pageSize;
  /**
   * 当前的页码
   */
  private Integer pageNo;
}
