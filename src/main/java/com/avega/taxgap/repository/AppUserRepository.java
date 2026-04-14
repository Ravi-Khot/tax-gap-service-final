package com.avega.taxgap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.taxgap.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
