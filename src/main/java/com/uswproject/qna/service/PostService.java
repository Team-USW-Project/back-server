package com.uswproject.qna.service;

import com.uswproject.qna.dto.PostCreateRequestDto;
import com.uswproject.qna.dto.PostDetailResponseDto;
import com.uswproject.qna.dto.PostListItemResponseDto;
import com.uswproject.qna.dto.PostUpdateRequestDto;
import com.uswproject.qna.entity.Post;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@RequiredArgsConstructor

public class PostService {

    private final PostRepository postRepository;


    //게시글 작성
    @Transactional
    public Long create(Long memberId, PostCreateRequestDto request) {
        Post post = new Post(memberId, request.getTitle(), request.getContent());
        return postRepository.save(post).getId();
    }

    //게시글 삭제
    @Transactional
    public void delete(Long memberId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + postId)
                );

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        post.softDelete();
    }

    //게시글 수정

    @Transactional
    public void update(Long memberId, Long postId, PostUpdateRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        // 작성자 검증
        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());

    }

    //게시글 조회

    @Transactional(readOnly = true)
    public List<PostListItemResponseDto> getPosts(Pageable pageable) {


        return postRepository.findAllByDeletedFalse(pageable)
                .getContent()
                .stream()
                .map(post -> new PostListItemResponseDto(post))
                .toList();

    }
    public PostDetailResponseDto getPost(Long postId) {

        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        return new PostDetailResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMemberId(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }

}