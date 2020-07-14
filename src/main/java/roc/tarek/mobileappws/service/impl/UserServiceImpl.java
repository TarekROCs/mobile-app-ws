package roc.tarek.mobileappws.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import roc.tarek.mobileappws.exceptions.UserServiceException;
import roc.tarek.mobileappws.io.entity.AddressEntity;
import roc.tarek.mobileappws.io.repositories.AddressRepository;
import roc.tarek.mobileappws.io.repositories.UserRepository;
import roc.tarek.mobileappws.io.entity.UserEntity;
import roc.tarek.mobileappws.service.UserService;
import roc.tarek.mobileappws.shared.Utils;
import roc.tarek.mobileappws.shared.dto.AddressDto;
import roc.tarek.mobileappws.shared.dto.UserDto;
import roc.tarek.mobileappws.ui.model.response.ErrorMessage;
import roc.tarek.mobileappws.ui.model.response.ErrorMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) throws Exception {

        if(userRepository.findByEmail(user.getEmail()) != null) throw new Exception("Record already exists");

        for(int i = 0; i < user.getAddresses().size(); i++){
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setAddressId(utils.generateAddressId(30));
            addressDto.setUserDetails(user);
            user.getAddresses().set(i, addressDto);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        for(int i =0; i<userEntity.getAddresses().size(); i++){
            userEntity.getAddresses().get(i).setUserEntity(userEntity);
        }

        UserEntity userStoredDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(userStoredDetails, UserDto.class);

        return returnValue;

    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null) throw new UsernameNotFoundException("user with id "+userId+" not found");

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);

        return userDto;
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {

        UserDto returnValue = new UserDto();

        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setEmail(userDto.getEmail());

        userEntity = userRepository.save(userEntity);
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.deleteById(userEntity.getId());
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        Pageable pageable = PageRequest.of(page, limit);

        Page<UserEntity> usersPage =  userRepository.findAll(pageable);

        List<UserEntity> userEntities = usersPage.getContent();

        for(UserEntity userEntity : userEntities){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public List<AddressDto> getUserAddresses(String userId) {

        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity user = userRepository.findByUserId(userId);
        if(user == null) return returnValue;

        Iterable<AddressEntity> userAddresses = addressRepository.findAllByUserEntity(user);


        for(AddressEntity addressEntity : userAddresses){
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) throws Exception {
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if(addressEntity == null) throw new Exception("address not found exception");

        ModelMapper modelMapper = new ModelMapper();
        AddressDto returnValue = modelMapper.map(addressEntity, AddressDto.class);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }



}
