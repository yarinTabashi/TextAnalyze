package com.yarin.rafael_exercise.analysis;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AnalysisRepository extends MongoRepository<AnalysisResult, String> {
    Optional<AnalysisResult> findByStr(String str);
}
