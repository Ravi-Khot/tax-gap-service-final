package com.avega.taxgap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avega.taxgap.entity.RuleDefinition;

public interface RuleDefinitionRepository extends JpaRepository<RuleDefinition, Long> {
    List<RuleDefinition> findByEnabledTrue();
}