package com.example.springstudy2.board.repository;

import com.example.springstudy2.board.entity.BoardEntity;
import com.example.springstudy2.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // select * from comment_table where board_id=? order by id desc;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);
}
