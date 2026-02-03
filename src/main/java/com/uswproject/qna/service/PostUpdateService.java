package com.uswproject.qna.service;

import com.uswproject.qna.dto.PostUpdateRequestDto;
import com.uswproject.qna.entity.Post;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUpdateService {

    private final PostRepository postRepository;

    @Transactional
    public void update(Long memberId, Long postId, PostUpdateRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        // 작성자 검증
        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());
        // save 안 해도 됨: JPA dirty checking으로 업데이트 반영됨
    }
}
