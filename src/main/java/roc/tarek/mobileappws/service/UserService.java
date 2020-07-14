package roc.tarek.mobileappws.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import roc.tarek.mobileappws.shared.dto.AddressDto;
import roc.tarek.mobileappws.shared.dto.UserDto;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user) throws Exception;
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(String userId, UserDto userDto);
    void deleteUser(String userId);
    List<UserDto> getUsers(int page, int limit);
    List<AddressDto> getUserAddresses(String userId);
    AddressDto getAddress(String addressId) throws Exception;
}
