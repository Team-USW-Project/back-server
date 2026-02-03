package com.uswproject.qna.service;

import com.uswproject.qna.dto.PostCreateRequestDto;
import com.uswproject.qna.dto.PostCreateResponseDto;
import com.uswproject.qna.entity.Post;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class PostService {

    private final PostRepository postRepository;



    @Transactional
    public PostCreateResponseDto create(Long memberId, PostCreateRequestDto request) {

        /*
        * TODO : 로그인 기능 연동 후 회원 검증 로직 추가
        * Member member = memberRepository .findById(memberId)
        *   .orElseThrow(() -> new IllegalArgumentException("회원가입한 사용자만 게시글 작성이 가능합니다."));
         */
        Post post = new Post(memberId, request.getTitle(), request.getContent());
        Long postId = postRepository.save(post).getId();
        return new PostCreateResponseDto(postId);
    }
}


