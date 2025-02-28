package test4.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import test4.entity.Faq;
import test4.repository.FaqRepository;
import test4.utils.PagingUtils;

/**
 * FAQ 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;

    // 모든 FAQ를 페이징 처리하여 반환합니다.
    public Page<Faq> getAllFAQs(Pageable pageable) {
        List<Faq> list = faqRepository.findAll();
        return PagingUtils.createPage(list, pageable);
    }

    // 삭제되지 않은 FAQ를 페이징 처리하여 반환합니다.
    public Page<Faq> getActiveFAQs(Pageable pageable) {
        List<Faq> list = faqRepository.findAllByDeletedYn('N');
        return PagingUtils.createPage(list, pageable);
    }

    // 삭제되지 않은 FAQ 리스트를 반환합니다.
    public List<Faq> getUserFAQs() {
        return faqRepository.findAllByDeletedYn('N');
    }

    // 특정 FAQ의 상세 정보를 반환합니다.
    public Faq faqDetail(Long id) {
        return faqRepository.findById(id).orElse(null);
    }

    // 특정 FAQ를 삭제 처리합니다.
    public void faqDelete(Long id){
        Faq faq = faqRepository.findById(id).orElse(null);
        faq.setDeletedYn('Y');
        faqRepository.save(faq);
    }

    // 새로운 FAQ를 생성합니다.
    public void faqCreate(String title, String question, String answer) {
        Faq faq = buildFaq(title, question, answer, 'N');
        faqRepository.save(faq);
    }

    // 기존 FAQ를 업데이트합니다.
    public void faqUpdate(Long id, String title, String question, String answer) {
        Faq faq = faqRepository.findById(id).orElse(null);
        faq.setTitle(title);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faqRepository.save(faq);
    }

    // FAQ 객체를 생성합니다.
    private Faq buildFaq(String title, String question, String answer, char deletedYn) {
        return Faq.builder()
                .title(title)
                .question(question)
                .answer(answer)
                .deletedYn(deletedYn)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
