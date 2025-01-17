package test4.service;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import test4.dto.AuthInfo;
import test4.entity.Apply;
import test4.entity.FileDetail;
import test4.repository.ApplyRepository;

/**
 * 상세 분석 신청서 서비스.
 * 신청서 생성, 조회, 수정, 삭제와 파일 처리를 담당.
 */
@RequiredArgsConstructor
@Service
public class ApplyService {
    private final ApplyRepository applyRepository;
    private final FileService fileService;

    public Page<Apply> getApplies(AuthInfo authInfo, Pageable pageable) {
        List<Apply> list = authInfo.getRole().equals("ADMIN")
                ? applyRepository.findAll(Sort.by(Sort.Order.desc("createdAt")))
                : applyRepository.findAllByUidOrderByCreatedAtDesc(authInfo.getId());
        return createPage(list, pageable);
    }
    
    public List<Apply> needAnswer() {
        return applyRepository.findAllByCompletedYnOrderByIdAsc('N');
    }
    
    private Page<Apply> createPage(List<Apply> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    /**
     * 첨부파일이 없는 신청서를 생성.
     *
     * @param author  작성자
     * @param title   제목
     * @param content 내용
     * @param authInfo 접속한 세션
     */
    @Transactional
    public void createBoard(String author, String title, String content, AuthInfo authInfo) {
        Apply apply = buildApply(author, title, content, authInfo);
        applyRepository.save(apply);
    }
    
    /**
     * 첨부파일이 없는 신청서를 생성.
     *
     * @param author  작성자
     * @param title   제목
     * @param content 내용
     * @param authInfo 접속한 세션
     */
    @Transactional
    public Long createBoardId(String author, String title, String content, AuthInfo authInfo) {
        Apply apply = buildApply(author, title, content, authInfo);
        apply.setCompletedYn('Y');
        Apply savedApply = applyRepository.save(apply);
        return savedApply.getId();
    }

    /**
     * 파일 첨부가 포함된 신청서를 생성.
     *
     * @param author  작성자
     * @param title   제목
     * @param content 내용
     * @param files   첨부 파일 배열
     * @param authInfo 접속한 세션
     * @throws IOException 파일 저장 실패 시 발생
     */
    @Transactional
    public void createBoard(String author, String title, String content, MultipartFile[] files, AuthInfo authInfo) throws IOException {
        Apply apply = buildApply(author, title, content, authInfo);

        if (files != null && files.length > 0) {
            List<FileDetail> fileDetails = fileService.uploadFiles(files);
            apply.setFiles(fileDetails);
        }

        applyRepository.save(apply);
    }

    // 신청서 상세정보 조회.
    @Transactional
    public Apply selectApplyDetail(Long id){
        return applyRepository.selectApplyDetail(id);
    }

    // 신청서 업데이트.
    @Transactional
    public void updateApply(Apply apply){
        Apply updateApply = applyRepository.selectApplyDetail(apply.getId());
        updateApply.setTitle(apply.getTitle());
        updateApply.setContent(apply.getContent());
        applyRepository.save(updateApply);
    }

    // 신청서 삭제
    @Transactional
    public void deleteApply(Long id){applyRepository.deleteById(id);}
    
    // 공통 로직: 신청서 객체 생성
    private Apply buildApply(String author, String title, String content, AuthInfo authInfo) {
        return Apply.builder()
                .author(author)
                .title(title)
                .content(content)
                .completedYn('N')
                .createdAt(LocalDateTime.now())
                .uid(authInfo.getId())
                .build();
    }
}
