package com.kcs.community.service.board;

import com.kcs.community.dto.board.BoardDetails;
import com.kcs.community.dto.board.BoardInfoDto;
import com.kcs.community.dto.user.UserInfoDto;
import com.kcs.community.entity.Board;
import com.kcs.community.entity.User;
import com.kcs.community.repository.board.BoardRepository;
import com.kcs.community.repository.user.UserRepository;
import com.kcs.community.service.S3ImageService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BoardInfoDto save(UserInfoDto user, String title, String content, String imagePath) {
        Optional<User> findUser = userRepository.findById(user.id());

        if (findUser.isEmpty()) {
            throw new NoSuchElementException("Not exists user");
        }

        Board board = Board.builder()
                .title(title)
                .content(content)
                .imageUrl(imagePath)
                .likeCount(0L)
                .viewCount(0L)
                .commentCount(0L)
                .user(findUser.get())
                .build();
        Board savedBoard = boardRepository.save(board);

        return BoardInfoDto.mapToDto(savedBoard);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BoardInfoDto> findAll() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardInfoDto::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BoardDetails findById(Long id) {
        Optional<Board> findBoard = boardRepository.findById(id);
        if (findBoard.isEmpty()) {
            throw new NoSuchElementException("Board not exists");
        }
        Board board = findBoard.get();
        return BoardDetails.mapToDto(board);
    }

    @Transactional
    @Override
    public BoardDetails update(Long id, Long userId, String title, String content, String imagePath) {
        Board board = getBoard(id, userId);

        if (!Objects.equals(userId, board.getUser().getId())) {
            throw new IllegalStateException("Not valid User");
        }

        board.update(title, content, imagePath);
        return BoardDetails.mapToDto(boardRepository.save(board));
    }

    @Transactional
    @Override
    public void delete(Long id, Long userId)  throws IOException {
        Board board = getBoard(id, userId);

        if (!userId.equals(board.getUser().getId())) {
            throw new IllegalStateException("Not valid User");
        }

        boardRepository.deleteById(id);
    }

    private Board getBoard(Long id, Long userId) {
        Optional<Board> findBoard = boardRepository.findById(id);
        if (findBoard.isEmpty()) {
            throw new NoSuchElementException("Board not exists");
        }
        Board board = findBoard.get();

        if (!board.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Not valid user");
        }
        return board;
    }
}
