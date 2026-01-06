package com.example.springstudy2.board.service;

import com.example.springstudy2.board.dto.BoardDTO;
import com.example.springstudy2.board.entity.BoardEntity;
import com.example.springstudy2.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity: boardEntityList){
            BoardDTO dto = BoardDTO.toBoardDTO(boardEntity);
            boardDTOList.add(dto);
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if(optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            return BoardDTO.toBoardDTO(boardEntity);
        }else{
            return null;
        }
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public void update(BoardDTO boardDTO) {
        BoardEntity boardEntity = boardRepository.findById(boardDTO.getId()).orElseThrow(()->
                new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        if(boardEntity.getBoardPass().equals(boardDTO.getUpdatePass())){
            boardEntity.update(boardDTO);
        }else{
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // 사용자가 요청한 페이지 번호 (0부터 시작하므로 1을 뺌)
        int pageLimit = 3; // 한 페이지에 보여줄 글 개수 (테스트를 위해 3개로 설정)

        // 1. DB에서 해당 페이지의 엔티티들을 가져옴 (id 기준으로 내림차순 정렬)
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 2. 엔티티를 DTO로 변환 (상태 정보까지 포함된 Page 객체로 변환)
        Page<BoardDTO> boardDTOS = boardEntities.map(BoardDTO::toBoardDTO);

        return boardDTOS;
    }
}
