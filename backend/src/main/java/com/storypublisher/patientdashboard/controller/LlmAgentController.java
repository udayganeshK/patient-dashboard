package com.storypublisher.patientdashboard.controller;

import com.storypublisher.patientdashboard.service.LlmAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/llm-agent")
public class LlmAgentController {
    @Autowired
    private LlmAgentService llmAgentService;

    @PostMapping("/ask")
    public ResponseEntity<?> askAgent(@RequestBody QuestionRequest request) {
        return ResponseEntity.ok(llmAgentService.answerQuestion(request.getQuestion()));
    }

    public static class QuestionRequest {
        private String question;
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }
}
