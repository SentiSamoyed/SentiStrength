package web.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import web.entity.po.RepoPO;

@Repository
public interface RepoRepository extends CrudRepository<RepoPO, Long> {
  RepoPO findByFullName(String fullName);
}
