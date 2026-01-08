package com.example.springstudy2.board.repository;

import com.example.springstudy2.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits = b.boardHits + 1 where b.id = :id")
    void updateHits(@Param("id") Long id);

    // 제목으로 검색: SELECT * FROM test WHERE board_title LIKE %keyword%
    Page<BoardEntity> findByBoardTitleContaining(String keyword, Pageable pageable);

    // 작성자로 검색: SELECT * FROM test WHERE board_writer LIKE %keyword%
    Page<BoardEntity> findByBoardWriterContaining(String keyword, Pageable pageable);
}
