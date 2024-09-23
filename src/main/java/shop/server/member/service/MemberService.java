package shop.server.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.server.auth.authorization.MemberAuthorityUtils;
import shop.server.basket.entity.Basket;
import shop.server.exception.error.member.MemberExMessage;
import shop.server.exception.error.member.MemberException;
import shop.server.member.dtos.MemberResponseDto;
import shop.server.member.dtos.MemberSaveDto;
import shop.server.member.dtos.MemberUpdateDto;
import shop.server.member.entity.Member;
import shop.server.member.mapper.MemberMapper;
import shop.server.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder encoder;
    private final MemberMapper mapper;
    private final MemberRepository repository;
    private final MemberAuthorityUtils authorityUtils;

    @Transactional
    public MemberResponseDto save(MemberSaveDto saveDto) {
        Map<Boolean, String> result = repository.checkExistIdOrNickName(saveDto);
        if (result.isEmpty()) {

            saveDto.setPassword(encoder.encode(saveDto.getPassword()));
            List<String> roles = authorityUtils.createRoles(saveDto.getId());
            Member saveRequest = mapper.memberSaveDtotoMember(saveDto, roles);
            Basket basket = Basket.builder().basketItems(new ArrayList<>()).build();
            saveRequest.mappingBasket(basket);
            Member newMember = repository.save(saveRequest);
            return mapper.membertoMemberResponseDto(newMember);

        }

        String idOrNick = result.get(Boolean.TRUE);
        if(idOrNick.equals("idError")) throw new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.ID_DUPLICATION);
        else if(idOrNick.equals("nickError")) throw new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.NICKNAME_DUPLICATION);
        else throw new MemberException(HttpStatus.INTERNAL_SERVER_ERROR, MemberExMessage.SERVER_ERROR);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto information(Long memberId) {
        return repository.findMemberToResponseDto(memberId)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, MemberExMessage.NOT_EXIST));
    }

    @CacheEvict(value = "member", key = "#id")
    public MemberResponseDto update(Long memberId, MemberUpdateDto requestDto, String id) {
        boolean nickNameExist = repository.existsByNickName(requestDto.getNickName());
        if (!nickNameExist) {
            Member findMember = findByMemberId(memberId);
            MemberUpdateDto updateDto = mapper.injectionValueIfNull(findMember, requestDto, encoder);
            findMember.updateInfo(updateDto);
            return mapper.membertoMemberResponseDto(findMember);
        } else
            throw new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.NICKNAME_DUPLICATION);
    }

    public MemberResponseDto addPoint(Long memberId, Integer point) {
        Member findMember = findByMemberId(memberId);
        findMember.addPoint(point);
        return mapper.membertoMemberResponseDto(findMember);
    }

    public MemberResponseDto deleteMember(Long memberId) {
        return repository.delete(memberId);
    }

    public Member findByMemberIdFetchOrderList(Long memberId) {
        return repository.findByIdFetchOrderList(memberId);
    }

    public boolean checkPoint(Member member, int price) {
        if (member.getPoint() < price) {
            throw new MemberException(HttpStatus.BAD_REQUEST, MemberExMessage.NOT_ENOUGH_POINT);
        }
        return true;
    }

    public Member findByMemberId(Long memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, MemberExMessage.NOT_EXIST));
    }

}
