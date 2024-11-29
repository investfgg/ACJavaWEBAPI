package com.access_control.controller;

import com.access_control.entity.TBProfile;
import com.access_control.service.TBProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/profiles" )
public class TBProfileController
{
    private final TBProfileService tbProfileServ;

    @Autowired
    public TBProfileController( TBProfileService tbProfileServ )
    {
        this.tbProfileServ =  tbProfileServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getAllProfiles()
    {
        return tbProfileServ.getAllProfiles();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getProfile( @PathVariable( "id" ) long idProfile )
    {
        return tbProfileServ.getProfile( idProfile );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createProfile( @RequestBody TBProfile tbProfile )
    {
        return tbProfileServ.createProfile( tbProfile );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deleteProfile( @PathVariable( "id" ) long idProfile )
    {
        return tbProfileServ.deleteProfile( idProfile );
    }
}