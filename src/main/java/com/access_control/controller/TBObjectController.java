package com.access_control.controller;

import com.access_control.entity.TBObject;

import com.access_control.service.TBObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/objects" )
public class TBObjectController
{
    private final TBObjectService tbObjServ;

    @Autowired
    public TBObjectController( TBObjectService tbObjServ )
    {
        this.tbObjServ = tbObjServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getObjects()
    {
        return tbObjServ.getAllObjects();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getObject( @PathVariable( "id" ) long idObj )
    {
        return tbObjServ.getObject( idObj );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createObject( @RequestBody TBObject tbObject )
    {
        return tbObjServ.createObject( tbObject );
    }

    @PutMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String updateObject( @PathVariable( "id" ) long idObj, @RequestBody TBObject tbObject )
    {
        return tbObjServ.updateObject( idObj, tbObject );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteObject( @PathVariable( "id" ) long idObj )
    {
        return tbObjServ.deleteObject( idObj );
    }
}