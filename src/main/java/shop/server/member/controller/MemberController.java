package shop.server.member.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shop.server.aop.annotation.TimeLog;
import shop.server.aop.aspect.TimeLogAspect;
import shop.server.auth.memberdetails.MemberDetails;
import shop.server.exception.error.member.MemberException;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.member.dtos.MemberUpdateDto;
import shop.server.member.service.MemberService;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;

@RestController
@RequestMapping("/myShop/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @TimeLog
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signUp(@RequestBody @Validated MemberSaveDto dto) {
        MemberResponseDto response = service.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @TimeLog
    @GetMapping("/info/{memberId}")
    public ResponseEntity<MemberResponseDto> getInfo(@AuthenticationPrincipal MemberDetails member,
                                                     @PathVariable @Positive Long memberId) {
        checkMember(member, memberId);
        MemberResponseDto response = service.information(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @TimeLog
    @PatchMapping("/update/{memberId}")
    public ResponseEntity<MemberResponseDto> updateInfo(@AuthenticationPrincipal MemberDetails member,
                                                        @PathVariable @Positive Long memberId,
                                                        @RequestBody @Validated MemberUpdateDto updateDto) {
        checkMember(member, memberId);
        if (updateDto == null) {
            throw new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.CHECK_YOUR_DATA);
        }
        MemberResponseDto response = service.update(memberId, updateDto, member.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @TimeLog
    @PatchMapping("/addPoint/{memberId}/{point}")
    public ResponseEntity<MemberResponseDto> addPoint(@AuthenticationPrincipal MemberDetails member,
                                                      @PathVariable @Positive Long memberId,
                                                      @PathVariable @Positive Integer point) {
        checkMember(member, memberId);
        MemberResponseDto response = service.addPoint(memberId, point);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @TimeLog
    @DeleteMapping("/delete/{memberId}")
    public ResponseEntity<MemberResponseDto> deleteMember(@AuthenticationPrincipal MemberDetails member,
                                          @PathVariable @Positive Long memberId) {
        checkMember(member, memberId);
        MemberResponseDto result = service.deleteMember(memberId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private boolean checkMember(MemberDetails member, Long memberId) {
        if(member.getRoles().contains("ADMIN")) return true;
        else if (!member.getMemberId().equals(memberId)) {
            throw new MemberException(HttpStatus.UNAUTHORIZED, MemberExMessage.UNAUTHORIZED);
        }
        return true;
    }
}
