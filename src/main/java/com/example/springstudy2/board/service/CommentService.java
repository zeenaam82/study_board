package com.example.springstudy2.board.service;

import com.example.springstudy2.board.dto.CommentDTO;
import com.example.springstudy2.board.entity.BoardEntity;
import com.example.springstudy2.board.entity.CommentEntity;
import com.example.springstudy2.board.repository.BoardRepository;
import com.example.springstudy2.board.repository.CommentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Long save(CommentDTO commentDTO) {
        /* 부모 엔티티(BoardEntity) 조회 */
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDTO.getBoardId());

        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            // DTO -> Entity 변환 (별도의 변환 메서드를 만들면 깔끔합니다)
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, boardEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
    }

    public List<CommentDTO> findAll(Long boardId){
        // 1. 부모 엔티티 조회
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        // 2. 해당 게시글의 댓글 목록 조회 (최신순)
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        // 3. Entity List -> DTO List 변환
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity: commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }

    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}
