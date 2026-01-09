package com.example.springstudy2.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="board_file_table")
public class BoardFileEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName; // 사용자가 올린 파일명 (예: 내사진.jpg)
    private String storedFileName;   // 서버에 저장될 이름 (예: 837261_내사진.jpg - 중복방지용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity; // 어떤 게시글의 파일인지 연결

    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFileName, String storedFileName) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);

        // 부모 엔티티(BoardEntity)를 넘겨주어 관계를 맺음 (외래키 연결)
        boardFileEntity.setBoardEntity(boardEntity);

        return boardFileEntity;
    }
}
