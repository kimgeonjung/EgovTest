package test4.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import test4.entity.Apply;
import test4.repository.ApplyRepository;

@Component
@RequiredArgsConstructor
public class AutoAddLinkToApply {
	@Value("${server.home.dir}")
    private String homeDir;
	
	private final ApplyRepository applyRepository;
	
	// 파일 경로 자동 추가	 
    public void addLinkToApply(Long applyId, String userId, String modelName) {
		Apply apply = applyRepository.findById(applyId).orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		
	// 결과 파일 경로 생성. 임의로 모드 이름까지 넣음
	// 사용자id_applyId_모델명.json
	// 이름 파라미터에 test넣으면 test_parallel.json
	// /home/ubuntu/kiosk/123_4_parallel.json <- 이런 값으로 들어가면 됨 도서관은 해야함
		if(modelName.equals("kiosk")) {
			String filePath = homeDir + modelName + "/" + userId + "_" + applyId + "_parallel.json";
			String testfilePath = homeDir + modelName + "/kiosk_output_parallel.json";
			apply.setLink(testfilePath);
		} else if(modelName.equals("library")) {
			String testfilePath = homeDir + modelName + "/library_results.json";
			apply.setLink(testfilePath);
		}
		applyRepository.save(apply);
	}
}
