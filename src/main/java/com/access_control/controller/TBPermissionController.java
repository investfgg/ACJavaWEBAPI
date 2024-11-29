package com.access_control.controller;

import com.access_control.entity.TBPermission;
import com.access_control.service.TBPermissionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( value = "/api/permissions" )
public class TBPermissionController
{
    private final TBPermissionService tbPermissionServ;

    public TBPermissionController( TBPermissionService tbPermissionServ )
    {
        this.tbPermissionServ = tbPermissionServ;
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE )
    public String getPermissions()
    {
        return tbPermissionServ.getAllPermissions();
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String getPermission( @PathVariable( "id" ) long idPermission )
    {
        return tbPermissionServ.getPermission( idPermission );
    }

    @PostMapping( value = "/create", produces = MediaType.APPLICATION_JSON_VALUE )
    public String createPermission( @RequestBody TBPermission tbPermission )
    {
        return tbPermissionServ.createPermission( tbPermission );
    }

    @PutMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String updatePermission( @PathVariable( "id" ) long idPermission, @RequestBody TBPermission tbPermission )
    {
        return tbPermissionServ.updatePermission( idPermission, tbPermission );
    }

    @DeleteMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public String deletePermission( @PathVariable( "id" ) long idPermission )
    {
        return tbPermissionServ.deletePermission( idPermission );
    }
}