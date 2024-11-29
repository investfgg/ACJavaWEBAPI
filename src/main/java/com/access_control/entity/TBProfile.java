package com.access_control.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table( name = "tb_profiles" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "Profiles_R", procedureName = "sp_Profiles_R",parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtProfile", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Profiles_C", procedureName = "sp_Profiles_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsersApps", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtAppsObjs", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsTypePerms", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Profiles_D", procedureName = "sp_Profiles_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtProfile", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Profiles_UsersApps", procedureName = "sp_Profiles_UsersApps", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserApp", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Profiles_AppsObjs", procedureName = "sp_Profiles_AppsObjs", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtAppObj", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Profiles_UsTypePerms", procedureName = "sp_Profiles_UsTypePerms", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsTypePerm", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
public class TBProfile
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "id_usersapps", nullable = false )
    private TBUserApp usersapps;

    @ManyToOne
    @JoinColumn( name = "id_appsobjs", nullable = false )
    private TBAppObj appsobjs;

    @ManyToOne
    @JoinColumn( name = "id_ustypeperms", nullable = false )
    private TBUsTypePerm ustypeperms;

    @Column( name = "created_at" )
    @JsonProperty( "created_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date createdAt;

    @Column( name = "deleted_at" )
    @JsonProperty( "deleted_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date deletedAt;
}