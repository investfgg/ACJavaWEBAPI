package com.access_control.controller;

import com.access_control.entity.TBUser;

import com.access_control.service.TBUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/users" )
public class TBUserController
{
    private final TBUserService tbUserServ;

    @Autowired
    public TBUserController( TBUserService tbUserServ )
    {
        this.tbUserServ = tbUserServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUsers()
    {
        return tbUserServ.getAllUsers();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUser( @PathVariable( "id" ) long idUser )
    {
        return tbUserServ.getUser( idUser );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createUser( @RequestBody TBUser tbUser )
    {
        return tbUserServ.createUser( tbUser );
    }

    @PutMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String updateUser( @PathVariable( "id" ) long idUser, @RequestBody TBUser tbUser )
    {
        return tbUserServ.updateUser( idUser, tbUser );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteUser( @PathVariable( "id" ) long idUser )
    {
        return tbUserServ.deleteUser( idUser );
    }

    @GetMapping( value = "/usraccessesbyuser", produces = MediaType.APPLICATION_JSON_VALUE )
    // Reason: Locating an User in the table UsrAccess where contains "n" registered users access.
    public String relUsrsUsers_UsrAccByUser()
    {
        return tbUserServ.report_UsrAccByUser();
    }

    @GetMapping( value = "/usraccessesbyuser/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    // Reason: Locating an User in the table UsrAccess where contains "n" registered users access.
    public String relUsrsUsers_IdUsrAccByUser( @PathVariable( "id" ) long idUser )
    {
        return tbUserServ.report_IdUsrAccByUser( idUser );
    }
}