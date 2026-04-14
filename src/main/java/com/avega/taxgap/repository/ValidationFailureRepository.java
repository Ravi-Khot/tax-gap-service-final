package com.avega.taxgap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.taxgap.entity.ValidationFailure;

public interface ValidationFailureRepository extends JpaRepository<ValidationFailure, Long> {
	
}
