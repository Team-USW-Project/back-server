package com.uswproject.qna.service;

import com.uswproject.qna.dto.PostCreateRequestDto;
import com.uswproject.qna.dto.PostDetailResponseDto;
import com.uswproject.qna.dto.PostListItemResponseDto;
import com.uswproject.qna.dto.PostUpdateRequestDto;
import com.uswproject.qna.entity.Post;
import com.uswproject.qna.entity.ReactionType;
import com.uswproject.qna.repository.PostReactionRepository;
import com.uswproject.qna.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;

    // 게시글 작성
    @Transactional
    public Long create(Long memberId, PostCreateRequestDto request) {
        Post post = new Post(memberId, request.getTitle(), request.getContent());
        return postRepository.save(post).getId();
    }

    // 게시글 삭제 (soft delete)
    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. id=" + postId));

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        post.softDelete();
    }

    // 게시글 수정
    @Transactional
    public void update(Long memberId, Long postId, PostUpdateRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());
    }

    // 게시글 목록 조회 (+ 좋아요/싫어요 수)
    @Transactional(readOnly = true)
    public List<PostListItemResponseDto> getPosts(Pageable pageable) {

        var page = postRepository.findAllByDeletedFalse(pageable);
        List<Post> posts = page.getContent();

        // 게시글이 없으면 바로 반환
        if (posts.isEmpty()) {
            return List.of();
        }

        // postId 목록 추출
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        // (postId, type) 별 count 집계 한번에 조회
        List<PostReactionRepository.PostReactionCountRow> rows =
                postReactionRepository.countByPostIdsGroupByType(postIds);

        // postId -> likeCount / dislikeCount 맵 만들기
        Map<Long, Long> likeMap = new HashMap<>();
        Map<Long, Long> dislikeMap = new HashMap<>();

        for (var row : rows) {
            if (row.getType() == ReactionType.LIKE) {
                likeMap.put(row.getPostId(), row.getCnt());
            } else if (row.getType() == ReactionType.DISLIKE) {
                dislikeMap.put(row.getPostId(), row.getCnt());
            }
        }

        // DTO 변환 (없으면 0)
        return posts.stream()
                .map(post -> new PostListItemResponseDto(
                        post,
                        likeMap.getOrDefault(post.getId(), 0L),
                        dislikeMap.getOrDefault(post.getId(), 0L)
                ))
                .toList();
    }

    // 게시글 상세 조회 (+ 좋아요/싫어요 수)
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPost(Long postId) {

        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));

        long likeCount = 0L;
        long dislikeCount = 0L;

        var typeCounts = postReactionRepository.countByPostIdGroupByType(postId);
        for (var tc : typeCounts) {
            if (tc.getType() == ReactionType.LIKE) likeCount = tc.getCnt();
            else if (tc.getType() == ReactionType.DISLIKE) dislikeCount = tc.getCnt();
        }

        return new PostDetailResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMemberId(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                likeCount,
                dislikeCount
        );
    }
}