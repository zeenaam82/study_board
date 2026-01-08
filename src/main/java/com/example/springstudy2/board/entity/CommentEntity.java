package com.example.springstudy2.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity {
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
}
