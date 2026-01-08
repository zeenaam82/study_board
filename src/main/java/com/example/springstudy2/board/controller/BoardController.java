package com.example.springstudy2.board.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import com.example.springstudy2.board.dto.BoardDTO;
import com.example.springstudy2.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/")
    public String list(@PageableDefault(page = 1) Pageable pageable,
                         @RequestParam(value = "searchType", required = false) String searchType,
                         @RequestParam(value = "keyword", required = false) String keyword,
                         Model model){
        Page<BoardDTO> boardList = boardService.paging(pageable);

        // keyword가 있으면 검색 로직, 없으면 전체 목록 로직
        if (keyword != null && !keyword.isEmpty()) {
            boardList = boardService.search(searchType, keyword, pageable);
        } else {
            boardList = boardService.paging(pageable);
        }

        // 공통 페이징 계산 로직 (기존과 동일)
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 검색어 유지를 위해 화면으로 다시 보내줌
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "list";
    }

    @GetMapping("/save")
    public String saveForm(){
        return "save";
    }

    @PostMapping("/save")
    public String save(BoardDTO boardDTO) throws IOException {
        System.out.println("BoardDTO = "+boardDTO);
        boardService.save(boardDTO);
        return "redirect:/board/";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        Cookie oldCookie = null;

        if(cookies != null){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("postView")){
                    oldCookie = cookie;
                }
            }
        }

        if(oldCookie != null){
            if(!oldCookie.getValue().contains("[" + id.toString() + "]")){
                boardService.updateHits(id);
                oldCookie.setValue(oldCookie.getValue() + "[" + id + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24); // 24시간
                response.addCookie(oldCookie);
            }
        }else{
            boardService.updateHits(id);
            Cookie newCookie = new Cookie("postView", "[" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); // 24시간
            response.addCookie(newCookie);
        }

        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model){
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(BoardDTO boardDTO, RedirectAttributes redirectAttributes){
        try{
            boardService.update(boardDTO);
            return "redirect:/board/" + boardDTO.getId();
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/board/update/" + boardDTO.getId();
        }
    }
}
