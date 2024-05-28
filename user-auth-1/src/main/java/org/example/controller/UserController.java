package org.example.controller;

import org.example.model.req.UserUpdateRequest;
import org.example.service.imp.UserServiceImp;
import org.example.util.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserServiceImp userServiceImp;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @GetMapping("detail")
    public ResponseEntity<?> getUserDetail() {
        var userDetails = authenticationFacade.getUser();
        var userRes = userServiceImp.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userRes);
    }

    @GetMapping("all")
    public ResponseEntity<?> listAll(){
        var listUser = userServiceImp.listAll();
        return ResponseEntity.ok(listUser);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable long id){
        var user = userServiceImp.getById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("update/{id}")
    public ResponseEntity<?> updateByid(@PathVariable long id, @RequestBody UserUpdateRequest updateRequest){
        userServiceImp.updateByid(id, updateRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteByid(@PathVariable long id){
        userServiceImp.deleteByid(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }



}
