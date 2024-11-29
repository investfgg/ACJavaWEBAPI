package com.access_control.controller;

import com.access_control.entity.TBUsrAccess;

import com.access_control.service.TBUsrAccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/usraccesses" )
public class TBUsrAccessController
{
    private final TBUsrAccessService tbUsrAccessServ;

    @Autowired
    public TBUsrAccessController( TBUsrAccessService tbUsrAccessServ )
    {
        this.tbUsrAccessServ = tbUsrAccessServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUsrAccesses()
    {
        return tbUsrAccessServ.getAllUsrAccesses();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUsrAccess( @PathVariable( "id" ) long idUsrAccess )
    {
        return tbUsrAccessServ.getUsrAccess( idUsrAccess );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createUsrAccess( @RequestBody TBUsrAccess tbUsrAccess )
    {
        return tbUsrAccessServ.createUsrAccess( tbUsrAccess );
    }

    @PutMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String updateUsrAccess( @PathVariable( "id" ) long idUsrAccess, @RequestBody TBUsrAccess tbUsrAccess )
    {
        return tbUsrAccessServ.updateUsrAccess( idUsrAccess, tbUsrAccess );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteUsrAccess( @PathVariable( "id" ) long idUsrAccess )
    {
        return tbUsrAccessServ.deleteUsrAccess( idUsrAccess );
    }

    @GetMapping( value = "/rel", produces = MediaType.APPLICATION_JSON_VALUE )
    public String reportUsrAccess()
    {
        return tbUsrAccessServ.reportUsrAccess();
    }
}