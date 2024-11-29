package com.access_control.controller;

import com.access_control.entity.TBUserApp;

import com.access_control.service.TBUserAppService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/usersapps" )
public class TBUserAppController
{
    private final TBUserAppService tbUserAppServ;

    @Autowired
    public TBUserAppController( TBUserAppService tbUserAppServ )
    {
        this.tbUserAppServ = tbUserAppServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserApps()
    {
        return tbUserAppServ.getAllUserApps();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserApp( @PathVariable( "id" ) long idUserApp )
    {
        return tbUserAppServ.getUserApp( idUserApp );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createUserApp( @RequestBody TBUserApp tbUserApp )
    {
        return tbUserAppServ.createUserApp( tbUserApp );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteUserApp( @PathVariable( "id" ) long idUserApp )
    {
        return tbUserAppServ.deleteUserApp( idUserApp );
    }
}