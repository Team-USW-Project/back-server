package com.uswproject.qna.service;

import com.uswproject.qna.dto.PostCreateRequestDto;
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
    public Long create(Long memberId, PostCreateRequestDto request) {
        Post post = new Post(memberId, request.getTitle(), request.getContent());
        return postRepository.save(post).getId();
    }
}


