package test4.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import test4.dto.AuthInfo;
import test4.repository.ApplyRepository;
import test4.service.ApplyService;

@Slf4j
@RequiredArgsConstructor
@Component
public class PythonExecutor {
	private final ApplyService applyService;
	private final ApplyRepository applyRepository;
	private final AutoAddLinkToApply linkToApply;
	
    /**
     * Python 스크립트를 실행하고 결과를 반환하는 메서드
     *
     * @param modelName 실행할 Python 모델의 이름
     * @return Python 스크립트 실행 결과
     */
    public String runPythonScript(AuthInfo authInfo, String modelName, String... parameters) {
        StringBuilder output = new StringBuilder();

        if (!modelName.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("잘못된 입력입니다.");
        } else if (!modelName.equals("kiosk") && !modelName.equals("library")) {
            throw new IllegalArgumentException("잘못된 모델 이름이 들어갔습니다.");
        }

        try {
        	// 매개변수를 포함한 명령어 작성
        	String paramStr = String.join(" ", parameters); // 빈 배열이면 paramStr은 빈 문자열
            // 명령어 작성
            String[] commands = {
                "bash", "-c",
                "cd /home/ubuntu/" + modelName + " && source ./venv/bin/activate && ./" + modelName + ".sh " + paramStr
            };
            log.info("Command to execute: {}", Arrays.toString(commands));

            // ProcessBuilder 초기화
            ProcessBuilder processBuilder = new ProcessBuilder(commands);

            // 환경 변수 설정
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":/home/ubuntu/" + modelName + "/venv/bin");
            processBuilder.environment().put("VIRTUAL_ENV", "/home/ubuntu/" + modelName + "/venv");

            // 프로세스 실행
            Process process = processBuilder.start();

            // 표준 출력 및 표준 에러 비동기 처리
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("{}", line);
                        output.append(line).append("\n");
                    }
                } catch (Exception e) {
                    log.error("Error reading Python stdout", e);
                }
            }).start();

            new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        log.error("{}", line);
                        output.append("Error: ").append(line).append("\n");
                    }
                } catch (Exception e) {
                    log.error("Error reading Python stderr", e);
                }
            }).start();

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            log.info("Python process exited with code: {}", exitCode);
            output.append("Python script exited with code: ").append(exitCode).append("\n");
            
            if (exitCode == 0) {
                // 게시글 생성
            	String applyTitle = authInfo.getName() + "님이 신청한 " + modelName + "분석 데이터입니다.";
                String applyContent = authInfo.getName() + "님이 신청한 " + modelName + "분석 데이터입니다. "
                		+ "\n자세한 내용은 하단 결과보기를 확인해 주세요";
                Long applyId = applyService.createBoardId(authInfo.getName(), applyTitle, applyContent, authInfo);
                // 게시글에 파일 경로 추가		   여기 authInfo에서 id 가져오는게 아니라 apply id값을 가져와야 함
                linkToApply.addLinkToApply(applyId, authInfo.getLoginId(), modelName);
            }

        } catch (Exception e) {
            log.error("Exception occurred while executing Python script", e);
            output.append("Exception: ").append(e.getMessage());
        }

        return output.toString();
    }
}

