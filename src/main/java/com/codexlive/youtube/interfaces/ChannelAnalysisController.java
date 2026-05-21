package com.codexlive.youtube.interfaces;

import com.codexlive.youtube.application.AnalyzeChannelCommand;
import com.codexlive.youtube.application.AnalyzeChannelUseCase;
import com.codexlive.youtube.application.ChannelAnalysisResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channel-analyses")
public class ChannelAnalysisController {

    private final AnalyzeChannelUseCase analyzeChannelUseCase;

    public ChannelAnalysisController(AnalyzeChannelUseCase analyzeChannelUseCase) {
        this.analyzeChannelUseCase = analyzeChannelUseCase;
    }

    @PostMapping
    public ResponseEntity<ChannelAnalysisResponse> create(@Valid @RequestBody ChannelAnalysisRequest request) {
        ChannelAnalysisResult result = analyzeChannelUseCase.analyze(new AnalyzeChannelCommand(request.channelUrl()));
        return ResponseEntity.ok(ChannelAnalysisResponse.from(result));
    }
}
