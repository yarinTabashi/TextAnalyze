package com.yarin.rafael_exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yarin.rafael_exercise.analysis.AnalysisController;
import com.yarin.rafael_exercise.analysis.AnalysisResult;
import com.yarin.rafael_exercise.analysis.AnalysisService;
import com.yarin.rafael_exercise.analysis.HistoryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalysisController.class)
public class AnalysisControllerTest {
    @Autowired
    private MockMvc mockMvc; // to mock http requests
    @MockBean
    private AnalysisService service;
    @Autowired
    private ObjectMapper objectMapper; // to convert objects to Json

    // Restore history object by his id, when it's really found in the db.
    @Test
    public void test_restoreHistoryObject_whenFound() throws Exception{
        AnalysisResult result = new AnalysisResult();
        result.setId("1");
        result.setStr("הקלט שלי");
        when(service.restoreAnalysis("1")).thenReturn(result);

        mockMvc.perform(get("/api/analysis")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.str").value("הקלט שלי"));
        verify(service, times(1)).restoreAnalysis("1");
    }

    // Try to restore history object by his id, when it doesn't found in the db.
    @Test
    public void test_restoreHistoryObject_whenNotFound() throws Exception{
        AnalysisResult result = null;
        when(service.restoreAnalysis("1")).thenReturn(null);
        mockMvc.perform(get("/api/analysis")
                        .param("id", "1"))
                .andExpect(status().isNotFound());

        verify(service, times(1)).restoreAnalysis("1");
    }

    // Get all the history DTOs that found in the db.
    @Test
    public void test_getHistory() throws Exception{
        List<HistoryDTO> historyDTOS = Arrays.asList(
                new HistoryDTO("1", "קלט ראשון"),
                new HistoryDTO("2", "קלט שני"),
                new HistoryDTO("3", "קלט שלישי")
        );

        when(service.fetchHistory()).thenReturn(historyDTOS);
        mockMvc.perform(get("/api/analysis/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[2].id").value("3"));
        verify(service, times(1)).fetchHistory();
    }

    // Analyze input when it's already exist in the db.
    @Test
    public void test_analyzeInput_whenExistsInHistory() throws Exception{
        AnalysisResult result = new AnalysisResult();
        result.setId("1");
        result.setStr("הקלט הראשון");
        when(service.restoreAnalysisByStr("הקלט הראשון")).thenReturn(result);

        mockMvc.perform(post("/api/analysis/analyze")
                        .param("st", "הקלט הראשון")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.str").value("הקלט הראשון"));

        verify(service, times(1)).restoreAnalysisByStr("הקלט הראשון");
    }

    // Analyze input when it doesn't exist in the db.
    @Test
    public void test_analyzeInput_whenNotExistsInHistory() throws Exception{
        AnalysisResult analysisResult = new AnalysisResult();

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

        analysisResult = AnalysisResult.builder()
                .id("100")
                .str("קלט לדוגמה")
                .lettersAppearances(lettersMap)
                .wordsAppearances(wordsMap)
                .wordsWithRepeatedLetters(0)
                .build();

        when(service.restoreAnalysis("קלט לדוגמה")).thenReturn(null);
        when(service.getAnalysis("קלט לדוגמה")).thenReturn(analysisResult);
        mockMvc.perform(post("/api/analysis/analyze")
                        .param("st", "קלט לדוגמה")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("100"))
                .andExpect(jsonPath("$.str").value("קלט לדוגמה"));

        verify(service, times(1)).restoreAnalysisByStr("קלט לדוגמה");
        verify(service, times(1)).getAnalysis("קלט לדוגמה");
        verify(service, times(1)).saveAnalysis(analysisResult);
    }

    // Analyze input when the input is too long (it needs to return Bad request status).
    @Test
    public void test_analyzeInput_whenLongInvalidInput() throws Exception{
        String st = "טקסט ארוך מאוד לדוגמה טקסט ארוך מאוד לדוגמה טקסט ארוך מאוד לדוגמה טקסט ארוך מאוד לדוגמה טקסט ארוך מאוד לדוגמה";
        mockMvc.perform(post("/api/analysis/analyze")
                        .param("st", st)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(0)).restoreAnalysis(anyString());
    }

    // Analyze input when the input includes invalid chars (it needs to return Bad request status).
    @Test
    public void test_analyzeInput_whenInvalidInput_withInvalidUniqueChars() throws Exception{
        String st = "טקסט לדוגמה$";
        mockMvc.perform(post("/api/analysis/analyze")
                        .param("st", st)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, times(0)).restoreAnalysis(anyString());
    }
}
