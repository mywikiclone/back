package com.example.posttest.repository;

import com.example.posttest.entitiy.LobContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LobRepository extends JpaRepository<LobContent,Long> {
}
