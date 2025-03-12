package com.example.posttest.repository.contentrepositories;


import com.example.posttest.entitiy.Content;
import com.example.posttest.etc.UserAdmin;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content,Long>{





 //@Modifying//업데이트 쿼리의 경우 이어노테이션이 필요함.
// @Query("update Content c set c.title=:titles,c.content=:contexts,c.Update_Time=:update_times where c.id=:id ")
// public int updatecontent(@Param("titles") String titles, @Param("contexts") String contexts, @Param("update_times")LocalDateTime update_times,@Param("id")Long id);


 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select c from Content c where c.content_id=:id")
 public Optional<Content> findbyidwithforupdate(@Param("id") Long id);



 @Query("select c from Content c where c.title=:title")
 public Optional<Content> findbytitle(@Param("title") String title);




 @Query("select c from Content c where c.title like %:titles% order by c.Create_Time desc")
 public Page<Content> search_logic(@Param("titles") String titles, Pageable pageable);



 @Query("select c from Content c order by c.Create_Time desc")
 public Page<Content> content_list( Pageable pageable);







 @Query(value = "select * from Content c order by random() limit 1",nativeQuery = true)
 public Optional<Content> random_logic();

 @Modifying(clearAutomatically=true)
 @Query("UPDATE Content c SET c.grade = :grade WHERE c.id = :id")
 int changeadmin(@Param("grade") UserAdmin grade, @Param("id") Long id);

}
