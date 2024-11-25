package com.example.posttest.repository.memrepo;

import com.example.posttest.entitiy.ChangeLog;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {



    @Query("select m from Member m where m.email = :email")
    public Optional<Member> findmember_beforeassign(@Param("email") String email);


    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.email=:name where m.id=:id")
    public int fixmember(@Param("id") Long id,@Param("name") String name);



    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.grade = :grade WHERE m.email = :email")
    public int changeadmin(@Param("grade")UserAdmin grade,@Param("email") String email);


    @Modifying(clearAutomatically=true)
    @Query("UPDATE Member m SET m.password = :password WHERE m.email = :email")
    public int changepassword(@Param("email") String email,@Param("password") String password);

}
