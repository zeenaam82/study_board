package com.example.springstudy2.board.dto;

import com.example.springstudy2.board.entity.BoardEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardPass;
    private String updatePass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private String boardCreatedAt;

    private MultipartFile boardFile; // save.html의 name="boardFile"과 일치해야 함
    private String originalFileName;
    private String storedFileName;
    private int fileAttached;

    private String dateFormat(LocalDateTime date) {
        if (date == null)
            return null;

        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedAt(boardDTO.dateFormat(boardEntity.getCreatedAt()));

        if (boardEntity.getFileAttached() == null || boardEntity.getFileAttached() == 0) {
            boardDTO.setFileAttached(0); // 파일 없음
        } else {
            boardDTO.setFileAttached(1); // 파일 있음

            // 파일 정보가 있을 때만 파일 이름을 가져옴 (방어 코드)
            if (!boardEntity.getBoardFileEntityList().isEmpty()) {
                boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
                boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
            }else {
                // fileAttached는 1인데 실제 파일 데이터가 없는 예외 상황
                boardDTO.setFileAttached(0);
            }
        }
        return boardDTO;
    }
}
