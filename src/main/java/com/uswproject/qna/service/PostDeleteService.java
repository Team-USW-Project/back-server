package com.uswproject.qna.service;

import com.uswproject.qna.entity.Post;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDeleteService {

    private final PostRepository postRepository;

    @Transactional
    public void delete(Long memberId, Long postId) {

        // 1. 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + postId)
                );

        // 2. 작성자 검증
        if (!post.getWriterMemberId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 3. 삭제
        postRepository.delete(post);
    }
}
