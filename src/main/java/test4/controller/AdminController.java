package test4.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import test4.dto.AuthInfo;
import test4.dto.MessageDto;
import test4.entity.FaQ;
import test4.entity.Member;
import test4.entity.Notice;
import test4.entity.QnA;
import test4.entity.QnAList;
import test4.repository.MemberRepository;
import test4.repository.QnARepository;
import test4.repository.QnA_ListRepository;
import test4.service.FAQService;
import test4.service.NoticeService;
import test4.service.QnAService;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final NoticeService noticeService;
    private final QnA_ListRepository qnAListRepository;
    private final MemberRepository memberRepository;
    private final QnARepository qnARepository;
    private final QnAService qnAService;
    private final FAQService faqService;

    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "/common/messageRedirect";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/adminPage";
    }


    @GetMapping("/admin_notice")
    public String noticeList(Model model,
                             @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<Notice> notices = noticeService.getAllNotices();
        final int start = (int) pageable.getOffset();
        final int end = Math.min(start + pageable.getPageSize(), notices.size());
        final Page<Notice> page = new PageImpl<>(notices.subList(start, end), pageable, notices.size());
        model.addAttribute("list", page); // 게시글 목록을 페이지 형식으로 모델에 추가
        return "admin/admin_notice";
    }

    @GetMapping("/admin_qna")
    public String qnaList(Model model) {
        List<QnA> qna = qnARepository.findAll();
        List<QnAList> qnALists = qnAListRepository.findAll();
        List<Member> members = memberRepository.findAll();
        model.addAttribute("qna", qna);
        model.addAttribute("qnaList", qnALists);
        model.addAttribute("member", members);
        return "admin/admin_qna";
    }

    @GetMapping("/admin/admin_qnaDetail")
    public String qnaDetail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("qna", qnAService.getInquiryDetail(id));
        model.addAttribute("lists", qnAService.getInquiryReplies(id));
        QnAList qnAList = qnAService.getInquiryDetail(id);
        Member member = memberRepository.findById(qnAList.getUid()).get();
        model.addAttribute("member", member);
        return "/admin/admin_qnaDetail";
    }

    @PostMapping("/admin/inquiryInsert")
    public String qnaInsert(@ModelAttribute QnAList list,
                            @RequestParam String content,
                            HttpSession session) {
        qnAService.insertInquiry(list, content, session);
        return "redirect:/admin/admin_qnaDetail";
    }

    @PostMapping("/admin/answerInsert")
    public String answerInsert(@RequestParam String content,
                               @RequestParam String role,
                               @RequestParam Long id) {
        qnAService.insertAnswer(content, role, id);
        return "redirect:/admin/admin_qnaDetail?id=" + id;
    }

    @PostMapping("/admin/end/{id}")
    public String end(@PathVariable Long id, Model model) {
        QnAList qnAList = qnAListRepository.findById(id).get();
        qnAList.setEndYn('Y');
        qnAListRepository.save(qnAList);
        MessageDto message = new MessageDto("문의를 종료하였습니다.", "/admin_qna", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }


    @GetMapping("/admin/admin_noticeWrite")
    public String noticeWrite(Model model) {
        return "admin/admin_noticeWrite";
    }

    @PostMapping("/admin/createNotice")
    public String createNotice(@RequestParam String title,
                               @RequestParam String content,
                               @RequestParam("files") MultipartFile[] files) throws IOException {

        // 서비스 호출하여 공지사항 생성
        noticeService.createNotice(title, content,files);

        // 공지사항 작성 후 관리자 페이지로 리디렉션
        return "redirect:/admin_notice";
    }

    @GetMapping("/admin/noticeDetail")
    public String adminNoticeDetail(@RequestParam Long id, Model model, HttpSession session) throws Exception {
        Notice notice = noticeService.selectNoticeDetail(id,false);  // 조회수 증가 처리가 서비스에서만 발생
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (authInfo != null) {
            model.addAttribute("authInfo", authInfo); // 템플릿에서 접근 가능하도록 모델에 추가
        }
        model.addAttribute("notice", notice);
        model.addAttribute("filePath", notice.getFilePaths());
        return "admin/admin_noticeDetail";  // 관리자 페이지
    }

    @PostMapping("/admin/updateNotice")
    public String updateNotice(Notice notice) {
        noticeService.updateNotice(notice);
        return "redirect:/admin_notice";
    }

    @PostMapping("/admin/deleteNotice")
    public String deleteNotice(@RequestParam Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin_notice";
    }

    @GetMapping("/admin_management")
    public String management(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "admin/admin_management";
    }

    @PostMapping("/admin/deleteMember")
    public ResponseEntity<String> deleteMember(@RequestBody Map<String, Object> request) {
        Long memberId = Long.valueOf(request.get("id").toString());
        memberRepository.deleteById(memberId);
        return ResponseEntity.ok("삭제 성공");
    }

    @GetMapping("/admin_faq")
    public String faq(Model model, @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<FaQ> lists = faqService.getFAQs(pageable);
        model.addAttribute("faqs", lists);
        return "/admin/admin_faq";
    }

    @GetMapping("/admin/admin_faqDetail")
    public String faqDetail(@RequestParam Long id, Model model) {
        FaQ faQ = faqService.faqDetail(id);
        model.addAttribute("faq", faQ);
        return "/admin/admin_faqDetail";
    }

    @PostMapping("/admin/admin_faqDelete")
    public String faqDelete(@RequestParam Long id, Model model) {
        faqService.faqDelete(id);
        MessageDto message = new MessageDto("삭제하였습니다", "/admin_faq", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }
    @GetMapping("/admin/admin_faqWrite")
    public String faqWrite() {
        return "admin/admin_faqWrite";
    }

    @PostMapping("/admin/createFAQ")
    public String createFAQ(@RequestParam String title,
                            @RequestParam String question,
                            @RequestParam String answer) {
        faqService.faqCreate(title, question, answer);
        return "redirect:/admin_faq";
    }

    @PostMapping("/admin/admin_faqUpdate")
    public String updateFAQ(@RequestParam Long id,
                            @RequestParam String title,
                            @RequestParam String question,
                            @RequestParam String answer) {
        faqService.faqUpdate(id, title, question, answer);
        return "redirect:/admin_faq";
    }
}
