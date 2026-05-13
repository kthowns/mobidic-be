package com.kthowns.mobidic.util;

import jakarta.persistence.EntityManager;

import java.util.List;

public interface CleanupStrategy {
    void cleanup(List<String> tableNames, EntityManager entityManager);
}