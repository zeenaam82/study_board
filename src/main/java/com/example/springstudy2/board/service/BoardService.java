package com.example.springstudy2.board.service;

import com.example.springstudy2.board.dto.BoardDTO;
import com.example.springstudy2.board.entity.BoardEntity;
import com.example.springstudy2.board.entity.BoardFileEntity;
import com.example.springstudy2.board.repository.BoardFileRepository;
import com.example.springstudy2.board.repository.BoardRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    public void save(BoardDTO boardDTO) throws IOException {
        // 1. 파일이 있는지 없는지 체크
        if (boardDTO.getBoardFile().isEmpty()) {
            // 파일이 없는 경우
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 파일이 있는 경우
        /*
            1. DTO에 담긴 파일을 꺼냄
            2. 파일의 이름 가져옴
            3. 서버 저장용 이름으로 만듦 (중복 방지용)
            4. 저장 경로 설정 및 실제 파일 저장
            5. board_table에 해당 데이터 save 처리
            6. board_file_table에 해당 데이터 save 처리
         */
            MultipartFile boardFile = boardDTO.getBoardFile(); // 1.
            String originalFilename = boardFile.getOriginalFilename(); // 2.
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3. (시간_파일명)

            // 4. 파일 저장 경로 설정 (본인의 경로로 수정 필요)
            String savePath = uploadPath + storedFileName;
            boardFile.transferTo(new File(savePath)); // 5. 실제 파일 저장

            // 6. 게시글(부모) 데이터 저장
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(savedId).get();

            // 7. 파일(자식) 데이터 저장
            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
            boardFileRepository.save(boardFileEntity);
        }
    }

    @Transactional(readOnly = true)
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

    @Transactional
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

    @Transactional(readOnly = true)
    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // 사용자가 요청한 페이지 번호 (0부터 시작하므로 1을 뺌)
        int pageLimit = 5; // 한 페이지에 보여줄 글 개수 (테스트를 위해 3개로 설정)

        // 1. DB에서 해당 페이지의 엔티티들을 가져옴 (id 기준으로 내림차순 정렬)
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 2. 엔티티를 DTO로 변환 (상태 정보까지 포함된 Page 객체로 변환)
        Page<BoardDTO> boardDTOS = boardEntities.map(BoardDTO::toBoardDTO);

        return boardDTOS;
    }

    @Transactional(readOnly = true)
    public Page<BoardDTO> search(String searchType, String keyword, Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 5;
        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id"));

        Page<BoardEntity> boardEntities;
        if ("boardTitle".equals(searchType)) {
            //trim 공백제거
            boardEntities = boardRepository.findByBoardTitleContaining(keyword.trim(), pageRequest);
        } else {
            boardEntities = boardRepository.findByBoardWriterContaining(keyword.trim(), pageRequest);
        }
        System.out.println("검색 타입: " + searchType);
        System.out.println("검색어: [" + keyword + "]");

        return boardEntities.map(BoardDTO::toBoardDTO);
    }
}
