package com.yarin.rafael_exercise.analysis;

/**
 * A DTO representing a history record. It contains just the id and the string.
 * If the user wants to retrieve a specific record, they can do so by using this ID to fetch the AnalysisResult.
 * */
public record HistoryDTO(String id, String label) {
}
