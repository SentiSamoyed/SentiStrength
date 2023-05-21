package web.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import web.entity.po.RepoPO;

@SpringBootTest
@Transactional
@Rollback
public class BasicTest {

  @Autowired
  RepoRepository repoRepository;

  @Test
  void basicTest() {
    var po = RepoPO.builder()
        .id(114514L)
        .name("jyy")
        .owner("os")
        .fullName("jyy/os")
        .htmlUrl("")
        .build();
    repoRepository.save(po);
    var po1 = repoRepository.findByFullName("jyy/os");
    Assertions.assertEquals(po, po1);
  }
}
