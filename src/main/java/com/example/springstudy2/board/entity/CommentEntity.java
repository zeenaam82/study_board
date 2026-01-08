package com.example.springstudy2.board.entity;

import com.example.springstudy2.board.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String commentWriter;

    @Column
    private String commentContents;

    /* 게시글과의 연관관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id") // DB 상의 외래키 이름
    private BoardEntity boardEntity; // 부모 엔티티를 참조

    public static CommentEntity toSaveEntity(CommentDTO commentDTO, BoardEntity boardEntity) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentWriter(commentDTO.getCommentWriter());
        commentEntity.setCommentContents(commentDTO.getCommentContents());
        commentEntity.setBoardEntity(boardEntity); // 부모 엔티티 설정 (외래키 역할)
        return commentEntity;
    }
}
