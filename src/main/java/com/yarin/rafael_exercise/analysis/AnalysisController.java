package com.yarin.rafael_exercise.analysis;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService service;

    /**
     * Endpoint to retrieve a specific analysis from the db, by the id.
     * @return ResponseEntity with the found AnalysisResult, or NOT-FOUND if not found.
     * */
    @GetMapping()
    public ResponseEntity<AnalysisResult> restoreHistoryObject(@RequestParam String id)
    {
        AnalysisResult analysisResult = service.restoreAnalysis(id);
        if (analysisResult == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analysisResult);
    }

    /**
     * Endpoint to get the last 15 records from the db.
     * @return ResponseEntity containing the list of history records (each one contains just the string, and it's id).
     * */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryDTO>> getHistory()
    {
        List<HistoryDTO> historyDTOs = service.fetchHistory();
        return ResponseEntity.ok(historyDTOs);
    }

    /**
     * Endpoint to analyze the given input.
     * @param st Hebrew string that may contain the symbols: dot, comma and question-mark.
     * @return ResponseEntity that contains the analysis, or a bad-request if fails.
     **/
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResult> analyzeInput(@RequestParam String st)
    {
        // Input validation (length and only Hebrew chars and ',.?')
        if (st.length() >= 100 || !st.matches("^[\\u0590-\\u05FF\\s,.?]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AnalysisResult analysis = service.restoreAnalysisByStr(st);
        if (analysis == null){
            analysis = service.getAnalysis(st);
        }
        service.saveAnalysis(analysis);
        return ResponseEntity.ok(analysis);
    }
}