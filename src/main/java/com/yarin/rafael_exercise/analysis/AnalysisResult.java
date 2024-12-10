package com.yarin.rafael_exercise.analysis;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class AnalysisResult {
    @Id
    private String id;
    private String str;
    private Map<Character, Integer> lettersAppearances;
    private Map<String, Integer> wordsAppearances;
    private int wordsWithRepeatedLetters;
}
