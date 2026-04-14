package com.avega.taxgap.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.avega.taxgap.entity.AppUser;
import com.avega.taxgap.entity.RuleDefinition;
import com.avega.taxgap.enums.RuleType;
import com.avega.taxgap.enums.Severity;
import com.avega.taxgap.repository.AppUserRepository;
import com.avega.taxgap.repository.RuleDefinitionRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RuleDefinitionRepository ruleDefinitionRepository,
                               AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
        	// Insert default rules if not already present
            if (ruleDefinitionRepository.count() == 0) {

                RuleDefinition highValueRule = RuleDefinition.builder()
                        .ruleName("High Value Transaction Rule")
                        .ruleType(RuleType.HIGH_VALUE_TRANSACTION)
                        .enabled(true)
                        .severity(Severity.HIGH)
                        .configJson("{\"threshold\":10000}")
                        .description("If transaction amount is greater than threshold, create exception")
                        .build();

                RuleDefinition refundRule = RuleDefinition.builder()
                        .ruleName("Refund Validation Rule")
                        .ruleType(RuleType.REFUND_VALIDATION)
                        .enabled(true)
                        .severity(Severity.MEDIUM)
                        .configJson("{\"note\":\"Refund amount should not exceed original sale amount\"}")
                        .description("Refund amount must not exceed original sale amount")
                        .build();

                RuleDefinition gstRule = RuleDefinition.builder()
                        .ruleName("GST Slab Violation Rule")
                        .ruleType(RuleType.GST_SLAB_VIOLATION)
                        .enabled(true)
                        .severity(Severity.HIGH)
                        .configJson("{\"slabThreshold\":5000,\"requiredTaxRate\":0.18}")
                        .description("If amount exceeds slab threshold, tax rate must be at least required tax rate")
                        .build();

                ruleDefinitionRepository.save(highValueRule);
                ruleDefinitionRepository.save(refundRule);
                ruleDefinitionRepository.save(gstRule);
            }
            
         // Insert default users if not present
            if (appUserRepository.count() == 0) {

                AppUser admin = AppUser.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .enabled(true)
                        .build();

                AppUser auditor = AppUser.builder()
                        .username("auditor")
                        .password(passwordEncoder.encode("auditor123"))
                        .role("ROLE_AUDITOR")
                        .enabled(true)
                        .build();

                appUserRepository.save(admin);
                appUserRepository.save(auditor);
            }
        };
    }
}
