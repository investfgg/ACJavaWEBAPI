package com.access_control.controller;

import com.access_control.entity.TBApplication;

import com.access_control.service.TBApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/applications" )
public class TBApplicationController
{
    private final TBApplicationService tbAppServ;

    @Autowired
    public TBApplicationController( TBApplicationService tbAppServ )
    {
        this.tbAppServ = tbAppServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getApplications()
    {
        return tbAppServ.getAllApplications();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getApplication( @PathVariable( "id" ) long idApp )
    {
        return tbAppServ.getApplication( idApp );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createApplication( @RequestBody TBApplication tbApplication )
    {
        return tbAppServ.createApplication( tbApplication );
    }

    @PutMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String updateApplication( @PathVariable( "id" ) long idApp, @RequestBody TBApplication tbApplication )
    {
        return tbAppServ.updateApplication( idApp, tbApplication );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteApplication( @PathVariable( "id" ) long idApp )
    {
        return tbAppServ.deleteApplication( idApp );
    }

    @GetMapping( value = "/usersbyapplication", produces = MediaType.APPLICATION_JSON_VALUE )
    // Reason: Locating an application in the table UsersApps where contains "n" registered users.
    public String relUsersApps_UsrByApp()
    {
        return tbAppServ.report_UsersApps_UsrByApp();
    }

    @GetMapping( value = "/usersbyapplication/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    // Reason: Locating an application in the table UsersApps where contains "n" registered users.
    public String relUsersApps_UsrByApp( @PathVariable( "id" ) long idApp )
    {
        return tbAppServ.report_UsersApps_IdUsrByApp( idApp );
    }
}