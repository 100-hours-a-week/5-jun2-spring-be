package com.kcs.community.service.user;

import com.kcs.community.dto.user.SignupRequest;
import com.kcs.community.dto.user.SignupResponse;
import com.kcs.community.dto.user.UserInfoDto;

public interface UserService {
    SignupResponse signup(SignupRequest request) throws IllegalArgumentException;

    UserInfoDto findByEmail(String email);

    UserInfoDto updateInfo(UserInfoDto userDto, String nickname, String imagePath);

    UserInfoDto updatePassword(UserInfoDto userDto, String password);

    void deleteById(UserInfoDto userDto);

    Boolean isDuplicatedEmail(String email);

    Boolean isDuplicatedNickname(String nickname);
}
