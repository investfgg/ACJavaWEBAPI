package com.access_control.controller;

import com.access_control.entity.TBUsTypePerm;
import com.access_control.service.TBUsTypePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/ustypeperms" )
public class TBUsTypePermController
{
    private final TBUsTypePermService tbUsTypePermServ;

    @Autowired
    public TBUsTypePermController( TBUsTypePermService tbUsTypePermServ )
    {
        this.tbUsTypePermServ = tbUsTypePermServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getAllUsTypePerms()
    {
        return tbUsTypePermServ.getAllUsTypePerms();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserApp( @PathVariable( "id" ) long idUsTypePerm )
    {
        return tbUsTypePermServ.getUsTypePerms( idUsTypePerm );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createUserApp( @RequestBody TBUsTypePerm tbUsTypePerm )
    {
        return tbUsTypePermServ.createUsTypePerms( tbUsTypePerm );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteUserApp( @PathVariable( "id" ) long idUsTypePerm )
    {
        return tbUsTypePermServ.deleteUsTypePerms( idUsTypePerm );
    }
}