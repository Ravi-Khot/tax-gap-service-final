package com.avega.taxgap.entity;

import com.avega.taxgap.enums.RuleType;
import com.avega.taxgap.enums.Severity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rule_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false, unique = true)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    private RuleType ruleType;

    @Column(nullable = false)
    private Boolean enabled;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    @Column(columnDefinition = "TEXT")
    private String description;
}
