package com.access_control.controller;

import com.access_control.entity.TBAppObj;

import com.access_control.service.TBAppObjService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/appsobjs" )
public class TBAppObjController
{
    private final TBAppObjService tbAppObjService;

    @Autowired
    public TBAppObjController( TBAppObjService tbAppObjService )
    {
        this.tbAppObjService = tbAppObjService;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserApps()
    {
        return tbAppObjService.getAllAppObjs();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getUserApp( @PathVariable( "id" ) long idAppObj )
    {
        return tbAppObjService.getAppObj( idAppObj );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createUserApp( @RequestBody TBAppObj tbAppObj )
    {
        return tbAppObjService.createAppObj( tbAppObj );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteUserApp( @PathVariable( "id" ) long idAppObj )
    {
        return tbAppObjService.deleteAppObj( idAppObj );
    }
}