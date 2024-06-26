package com.kcs.community.service.user;

import com.kcs.community.dto.user.SignupRequest;
import com.kcs.community.dto.user.SignupResponse;
import com.kcs.community.dto.user.UserInfoDto;
import com.kcs.community.entity.RoleType;
import com.kcs.community.entity.User;
import com.kcs.community.repository.board.BoardRepository;
import com.kcs.community.repository.comment.CommentRepository;
import com.kcs.community.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) throws IllegalArgumentException {
        // 중복 이메일, 닉네임 확인 로직
        String email = request.email();
        String nickname = request.nickname();

        validateDuplicatedInfo(email, nickname);

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .profileUrl(request.imagePath())
                .role(RoleType.USER)
                .createdAt(LocalDateTime.now().withNano(0))
                .updatedAt(LocalDateTime.now().withNano(0))
                .build();
        userRepository.save(user);

        log.info("signup success - email: {}, nickname: {}, role: {}", user.getEmail(), user.getNickname(),
                user.getRole());
        // TO-BE builder 변경 예정
        return new SignupResponse(user.getEmail(), user.getNickname());
    }

    @Override
    public UserInfoDto findByEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        if (findUser.isEmpty()) {
            throw new NoSuchElementException("Not Exists User");
        }
        return UserInfoDto.mapToDto(findUser.get());
    }

    @Override
    @Transactional
    public UserInfoDto updateInfo(UserInfoDto userDto, String updatedNickname, String imagePath) {
        Optional<User> findUser = userRepository.findById(userDto.id());
        if (findUser.isEmpty()) {
            throw new NoSuchElementException("Not Exists User");
        }

        User user = findUser.get();
        User saveUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(updatedNickname)
                .profileUrl(imagePath)
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now().withNano(0))
                .role(user.getRole())
                .build();

        userRepository.updateUser(saveUser.getId(), saveUser);
        return UserInfoDto.mapToDto(saveUser);
    }

    @Override
    @Transactional
    public UserInfoDto updatePassword(UserInfoDto userDto, String updatePassword) {
        Optional<User> findUser = userRepository.findById(userDto.id());
        if (findUser.isEmpty()) {
            throw new NoSuchElementException("Not Exists User");
        }

        User user = findUser.get();
        User saveUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(passwordEncoder.encode(updatePassword))
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now().withNano(0))
                .role(user.getRole())
                .build();
        userRepository.updateUser(saveUser.getId(), saveUser);
        return UserInfoDto.mapToDto(saveUser);
    }

    @Override
    @Transactional
    public void deleteById(UserInfoDto userDto) {
        commentRepository.deleteAllByUserId(userDto.id());
        boardRepository.deleteAllByUserId(userDto.id());
        userRepository.deleteUser(userDto.id());
    }

    @Override
    public Boolean isDuplicatedEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        return findUser.isPresent();
    }

    @Override
    public Boolean isDuplicatedNickname(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        return findUser.isPresent();
    }

    private void validateDuplicatedInfo(String email, String nickname) {
        try {
            userRepository.existsByEmail(email);
            userRepository.existsByNickname(nickname);
        } catch (EmptyResultDataAccessException e) {
            return;
        }
        throw new IllegalArgumentException("Duplicated User Information Error");
    }
}
