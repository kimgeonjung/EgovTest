package test4.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import test4.entity.FaQ;
import test4.repository.FAQRepository;

@Service
@RequiredArgsConstructor
public class FAQService {
    private final FAQRepository faqRepository;

    public Page<FaQ> getFAQs(Pageable pageable) {
        List<FaQ> list = faqRepository.findAll();
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public Page<FaQ> getUserFAQs(Pageable pageable) {
        List<FaQ> list = faqRepository.findAllByDeletedYn("N");
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public FaQ faqDetail(Long id) {
        return faqRepository.findById(id).orElse(null);
    }

    public void faqDelete(Long id){
        FaQ faq = faqRepository.findById(id).orElse(null);
        faq.setDeletedYn("Y");
        faqRepository.save(faq);
    }

    public void faqCreate(String title, String question, String answer){
        FaQ faq = new FaQ();
        faq.setTitle(title);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setDeletedYn("N");
        faq.setCreatedAt(LocalDateTime.now());
        faqRepository.save(faq);
    }

    public void faqUpdate(Long id, String title, String question, String answer) {
        FaQ faq = faqRepository.findById(id).orElse(null);
        faq.setTitle(title);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faqRepository.save(faq);
    }
}
