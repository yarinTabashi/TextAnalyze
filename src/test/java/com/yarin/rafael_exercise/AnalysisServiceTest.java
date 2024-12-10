package com.yarin.rafael_exercise;

import com.yarin.rafael_exercise.analysis.AnalysisRepository;
import com.yarin.rafael_exercise.analysis.AnalysisResult;
import com.yarin.rafael_exercise.analysis.AnalysisService;
import com.yarin.rafael_exercise.analysis.HistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;

public class AnalysisServiceTest {
    @InjectMocks
    private AnalysisService analysisService;
    @Mock
    private AnalysisRepository repository;
    private AnalysisResult analysisResult;

    @BeforeEach
    public void setup(){
        Map<Character, Integer> lettersMap = Map.of(
                'ק', 1,
                'ל', 2,
                'ט', 1,
                'ד', 1,
                'ג', 1,
                'מ', 1,
                'ה', 1
                );
        Map<String, Integer> wordsMap = Map.of(
                "קלט", 1,
                "לדוגמה", 1
        );

        MockitoAnnotations.openMocks(this);
        analysisResult = new AnalysisResult();
        analysisResult.setId("1");
        analysisResult.setStr("קלט לדוגמה");
        analysisResult.setLettersAppearances(lettersMap);
        analysisResult.setWordsAppearances(wordsMap);
        analysisResult.setWordsWithRepeatedLetters(0);
    }

    // "Fetch"  history from the db (using the service), and verify it returns the expected data.
    @Test
    public void test_fetchHistory_shouldReturnHistory(){
        List<AnalysisResult> lst = new ArrayList<>();
        lst.add(analysisResult);
        when(repository.findAll()).thenReturn(lst);

        List<HistoryDTO> result = analysisService.fetchHistory();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).id());
        assertEquals("קלט לדוגמה", result.get(0).label());
        verify(repository, times(1)).findAll();
    }

    // Restore an Analysis Object by ID when it exists in the db.
    @Test
    public void test_restoreAnalysis_shouldReturnAnalysisObj_whenFound(){
        when(repository.findById("1")).thenReturn(Optional.of(analysisResult));
        AnalysisResult result = analysisService.restoreAnalysis("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("קלט לדוגמה", result.getStr());

        assertEquals(7, result.getLettersAppearances().size());
        assertEquals(1, result.getLettersAppearances().get('ק'));
        assertEquals(2, result.getLettersAppearances().get('ל'));

        assertEquals(2, result.getWordsAppearances().size());
        assertEquals(1, result.getWordsAppearances().get("קלט"));
        assertEquals(1, result.getWordsAppearances().get("לדוגמה"));

        verify(repository, times(1)).findById("1");
    }

    // Calling the restore function when the ID is not found in the db.
    @Test
    public void test_restoreAnalysis_shouldReturnAnalysisObj_whenNotFound(){
        when(repository.findById("1")).thenReturn(Optional.empty());
        AnalysisResult result = analysisService.restoreAnalysis("1");
        assertNull(result);
        verify(repository, times(1)).findById("1");
    }

    // Get an analysis object from the service based on the given string (when it contains only Hebrew chars).
    @Test
    public void test_getAnalysis_shouldReturnAnalysisObj(){
        String st = "פרפר נחמד";
        AnalysisResult result = analysisService.getAnalysis(st);

        assertNotNull(result);
        assertEquals(st, result.getStr());

        assertEquals(6, result.getLettersAppearances().size());
        assertEquals(2, result.getLettersAppearances().get('פ'));
        assertEquals(2, result.getLettersAppearances().get('ר'));
        assertEquals(1, result.getLettersAppearances().get('נ'));
        assertEquals(1, result.getLettersAppearances().get('ח'));
        assertEquals(1, result.getLettersAppearances().get('מ'));
        assertEquals(1, result.getLettersAppearances().get('ד'));

        assertEquals(1, result.getWordsAppearances().get("פרפר"));
        assertEquals(1, result.getWordsAppearances().get("נחמד"));
        assertEquals(1, result.getWordsWithRepeatedLetters());
    }

    // Get an analysis object from the service based on the given string, that including unique valid chars.
    // This test ensures that symbols in the input don't affect.
    @Test
    public void test_getAnalysis_shouldReturnAnalysisObj_whenInputIncludesSymbols(){
        String st = "פרפר נחמד. ,";
        AnalysisResult result = analysisService.getAnalysis(st);

        assertNotNull(result);
        assertEquals(st, result.getStr());

        assertEquals(6, result.getLettersAppearances().size());
        assertEquals(2, result.getLettersAppearances().get('פ'));
        assertEquals(2, result.getLettersAppearances().get('ר'));
        assertEquals(1, result.getLettersAppearances().get('נ'));
        assertEquals(1, result.getLettersAppearances().get('ח'));
        assertEquals(1, result.getLettersAppearances().get('מ'));
        assertEquals(1, result.getLettersAppearances().get('ד'));

        assertEquals(1, result.getWordsAppearances().get("פרפר"));
        assertEquals(1, result.getWordsAppearances().get("נחמד"));
        assertEquals(1, result.getWordsWithRepeatedLetters());
    }
}