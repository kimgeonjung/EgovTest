package test4.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import test4.dto.AuthInfo;
import test4.service.ApplyService;
import test4.service.TaskService;
import test4.utils.PythonExecutor;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
	private final ApplyService applyService;
	private final TaskService taskService;
	private final PythonExecutor pythonExecutor;
	
	/**
     * 모델 이름만 받는 경우
     */
//	@PostMapping("/run/{modelName}")
//	public ResponseEntity<String> runPythonScript(@PathVariable String modelName) {
//	    if (modelName == null || modelName.isEmpty()) {
//	        return ResponseEntity.badRequest().body("modelName is required");
//	    }
//
//	    CompletableFuture.runAsync(() -> pythonExecutor.runPythonScript(modelName));
//
//	    return ResponseEntity.ok("완료까지 약 30분정도 소요됩니다.");
//	}
	
	/**
     * 모델 이름과 추가 매개변수를 받는 경우
     */
    @PostMapping("/run/{modelName}/with-params")
    public ResponseEntity<String> runPythonScriptWithParams(HttpSession session,
            @PathVariable String modelName,
            @RequestBody Map<String, String> parameters) {
    	AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
    	String userId = authInfo.getLoginId();
        if (modelName == null || modelName.isEmpty()) {
            return ResponseEntity.badRequest().body("modelName is required");
        }

        // 실행 제한 확인
        if (!taskService.isExecutionAllowed(userId)) {
            long remainingMillis = taskService.getRemainingTime(userId);
            long remainingMinutes = remainingMillis / 1000 / 60; // 남은 시간 (분 단위)

            if (remainingMinutes < 1) {
                return ResponseEntity.status(429) // Too Many Requests
                        .body("분석 가능 시간까지 약 1분 미만으로 남았습니다.");
            } else {
                return ResponseEntity.status(429) // Too Many Requests
                        .body("분석 가능 시간까지 약 " + remainingMinutes + "분 남았습니다.");
            }
        }

        // 매개변수 처리: Map의 값만 사용
        String[] paramArray = parameters.values().toArray(new String[0]);
        
        CompletableFuture.runAsync(() -> pythonExecutor.runPythonScript(authInfo, modelName, paramArray));
        
        return ResponseEntity.ok("완료까지 약 30분정도 소요됩니다.");
    }
}
