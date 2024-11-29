package com.access_control.controller;

import com.access_control.entity.TBUserType;
import com.access_control.service.TBUserTypeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/usertypes" )
public class TBUserTypeController
{
    private final TBUserTypeService tbUserTypeServ;

    public TBUserTypeController( TBUserTypeService tbUserTypeServ )
    {
        this.tbUserTypeServ = tbUserTypeServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserTypes()
    {
        return tbUserTypeServ.getAllUserTypes();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserType( @PathVariable( "id" ) long idUserType )
    {
        return tbUserTypeServ.getUserType( idUserType );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createUserType( @RequestBody TBUserType tbUserType )
    {
        return tbUserTypeServ.createUserType( tbUserType );
    }

    @PutMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String updateUserType( @PathVariable( "id" ) long idUserType, @RequestBody TBUserType tbUserType )
    {
        return tbUserTypeServ.updateUserType( idUserType, tbUserType );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteUserType( @PathVariable( "id" ) long idUserType )
    {
        return tbUserTypeServ.deleteUserType( idUserType );
    }
}