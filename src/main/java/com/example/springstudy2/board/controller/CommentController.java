package com.example.springstudy2.board.controller;

import com.example.springstudy2.board.dto.CommentDTO;
import com.example.springstudy2.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    public @ResponseBody List<CommentDTO> save(@ModelAttribute CommentDTO commentDTO){
        System.out.println("commentDTO = " + commentDTO);

        Long saveResult = commentService.save(commentDTO);

        if(saveResult != null){
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
            return commentDTOList;
        }else{
            return null;
        }
    }

    @PostMapping("/delete")
    public @ResponseBody List<CommentDTO> delete(@RequestParam("id") Long id, @RequestParam("boardId") Long boardId) {
        commentService.delete(id); // 서비스에 삭제 요청

        // 삭제 후 최신 댓글 목록을 다시 가져와서 리턴함 (화면 갱신용)
        List<CommentDTO> commentDTOList = commentService.findAll(boardId);
        return commentDTOList;
    }
}
