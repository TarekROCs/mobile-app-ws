package roc.tarek.mobileappws.ui.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import roc.tarek.mobileappws.exceptions.UserServiceException;
import roc.tarek.mobileappws.service.UserService;
import roc.tarek.mobileappws.shared.dto.AddressDto;
import roc.tarek.mobileappws.shared.dto.UserDto;
import roc.tarek.mobileappws.ui.model.request.AddressRequestModel;
import roc.tarek.mobileappws.ui.model.request.UserDetailsRequestModel;
import roc.tarek.mobileappws.ui.model.response.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("users") // endpoint: http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String userId) {

        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(userId);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping(path = "/{userId}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();

        if(!userId.isEmpty()) throw new UserServiceException("hello, I'm your userServiceException");

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String userId) {
        OperationStatusModel returnValue = new OperationStatusModel();

        returnValue.setOperationName(RequestOperationName.DELETE.name());

        // if failed
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());


        userService.deleteUser(userId);

        // if success
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "2") int limit){

        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> userDtos = userService.getUsers(page, limit);

        for (UserDto userDto : userDtos){
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;

    }


    @GetMapping(path="/{userId}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String userId)  throws Exception {
        List<AddressRest> returnValue = new ArrayList<>();
        List<AddressDto> userAddresses = userService.getUserAddresses(userId);

        Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId)).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withSelfRel();

        ModelMapper modelMapper = new ModelMapper();
        for (AddressDto addressDto : userAddresses){
            returnValue.add(modelMapper.map(addressDto, AddressRest.class));
        }

        Link selfLink;
        for (AddressRest addressRest : returnValue){
            selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddress(userId, addressRest.getAddressId())).withSelfRel();
            addressRest.add(selfLink);
        }

        return CollectionModel.of(returnValue, Arrays.asList(userLink, userAddressesLink));
    }

    @GetMapping(path="/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressRest> getAddress(@PathVariable String userId, @PathVariable String addressId) throws Exception {

        Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId)).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withRel("user addresses");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getAddress(userId, addressId)).withSelfRel();

        AddressDto addresseDto = userService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressRest returnValue = modelMapper.map(addresseDto, AddressRest.class);

//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }



}
