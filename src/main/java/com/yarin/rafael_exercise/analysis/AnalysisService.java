package com.yarin.rafael_exercise.analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final AnalysisRepository repository;

    /**
     * Retrieve the history of the analysis results (At any given time, up to 15 are reserved)
     * @return HistoryDTO contains just the string and the analysis id
     * */
    public List<HistoryDTO> fetchHistory(){
        List<AnalysisResult> lst = repository.findAll();
        List<HistoryDTO> historyDTOs = lst.stream()
                .map(analysisResult -> new HistoryDTO(analysisResult.getId(), analysisResult.getStr()))
                .collect(Collectors.toList());
        return historyDTOs;
    }

    /**
     * Retrieve an analysis result according the analysis id.
     * @return analysis object if found / null
     * */
    public AnalysisResult restoreAnalysis(String id){
        Optional<AnalysisResult> analysis = repository.findById(id);
        return analysis.orElse(null);
    }

    /**
     * Retrieve an analysis result by string.
     * @return analysis object if found / null
     * */
    public AnalysisResult restoreAnalysisByStr(String st){
        Optional<AnalysisResult> analysis = repository.findByStr(st);
        return analysis.orElse(null);
    }


    /**
     * Saves the given analysis result, and delete the oldest (if there are already 15 records).
     * */
    public void saveAnalysis(AnalysisResult analysis){
        List<AnalysisResult> analysisList = repository.findAll();
        if (analysisList.size() >= 15){
            repository.delete(analysisList.getFirst());
        }

        repository.save(analysis);
    }

    /**
     * Analyze the given string
     * @return AnalysisResult object
     * */
    public AnalysisResult getAnalysis(String st){
        AnalysisResult analysis = new AnalysisResult();
        analysis.setStr(st);
        st = ignoreSymbols(st);
        analysis.setLettersAppearances(countLettersAppearances(st));
        analysis.setWordsAppearances(countWordsAppearances(st));
        analysis.setWordsWithRepeatedLetters(countWordsWithRepeatedLetters(st));

        return analysis;
    }

    /**
     * Removes the symbols: dot, comma and question-mark.
     * @return Returns the same string without this chars.
     * */
    private String ignoreSymbols(String st) {
        return st.replaceAll("[.,?]", "");
    }


    /**
     * Counting Hebrew letter appearances
     * @return A map containing Hebrew letters (if present in the string) and their counts as values.
     * */
    private Map<Character, Integer> countLettersAppearances(String st){
        Map<Character, Integer> lettersMap = new HashMap<>();
        Character c;

        for (int i = 0; i < st.length(); i++)
        {
            c = st.charAt(i);
            if (c >= '\u05D0' && c <= '\u05EA') {
                lettersMap.put(c, lettersMap.getOrDefault(c, 0) + 1);
            }
        }
        return lettersMap;
    }

    /**
     * Counting words appearances
     * @return A map containing words (if present in the string) and their counts as values. It ignores from symbols.
     * */
    private Map<String, Integer> countWordsAppearances(String st){
        Map<String, Integer> wordsAppearsMap = new HashMap<>();
        String[] splitted = st.split(" ");

        for (String s : splitted) {
            wordsAppearsMap.put(s, wordsAppearsMap.getOrDefault(s, 0) + 1);
        }

        return wordsAppearsMap;
    }

    /**
     * Checks if the word contains repeated letters.
     * @return true if has repeated letters, else - false
     * */
    private boolean hasRepeatedLetter(String word){
        Set<Character> s = new HashSet<>();

        for (int i = 0; i < word.length(); i++){
            if (!s.add(word.charAt(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * Counting the number of words in the input that includes repeated letters.
     * @return the num of words that contains repeated letters
     * */
    private int countWordsWithRepeatedLetters(String st){
        int amount = 0;

        String[] splitted = st.split(" ");
        for (String s : splitted) {
            if (hasRepeatedLetter(s)) {
                amount++;
            }
        }

        return amount;
    }
}
