package com.example.posttest.repository;

import com.example.posttest.entitiy.UserAdmins;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminRepo extends JpaRepository<UserAdmins,Long> {
}
