package com.example.posttest.repository;

import com.example.posttest.entitiy.MemberFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<MemberFeed,Long> {
}
