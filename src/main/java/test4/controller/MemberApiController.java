package test4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import test4.repository.MemberRepository;
import test4.service.MemberService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @PostMapping("/confirmId")
    public ResponseEntity<Boolean> confirmId(@RequestBody String loginId) {
        log.info("Confirm Id: {}", loginId);
        boolean result = memberService.registerCheckId(loginId);
        log.info("성공여부: {}", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/findLoginId")
    public ResponseEntity<String> findLoginId(@RequestParam String name, @RequestParam String email) {
        String loginId = memberService.findLoginIdByNameAndEmail(name, email);
        if (loginId != null) {
            return ResponseEntity.ok(loginId);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 정보를 찾을 수 없습니다.");
        }
    }

//    @PostMapping("/findPassword")
//    public ResponseEntity<String> findPassword(@RequestBody FindPwRequest request) {
//        String status;
//        try{
//            status = memberService.findPw(request);
//        } catch (Exception e) {
//            status = e.getMessage();
//        }
//        return ResponseEntity.ok().body(status);
//    }
//
//    @PostMapping("/changePw")
//    public ResponseEntity<String> changePw(@RequestBody ChangePwRequest request, HttpSession session) {
//        try{
//            String response = memberService.changePassword(request);
//            session.invalidate();
//            return ResponseEntity.ok().body(response);
//        }catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}
