package roc.tarek.mobileappws.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import roc.tarek.mobileappws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user) throws Exception;
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(String userId, UserDto userDto);
}
