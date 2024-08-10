package com.example.posttest.repository.memrepo;

import com.example.posttest.entitiy.ChangeLog;
import com.example.posttest.entitiy.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {



    @Query("select m from Member m where m.email=:email and m.password=:password")
    public Optional<Member> findmember(@Param("email") String email, @Param("password")String password);


    @Query("select m from Member m where m.email=:email")
    public Optional<Member> findmember_beforeassign(@Param("email") String email);

}
