package com.example.springstudy2.board.repository;

import com.example.springstudy2.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<BoardEntity, Long> {
}
