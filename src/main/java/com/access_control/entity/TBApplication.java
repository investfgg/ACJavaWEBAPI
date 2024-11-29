package com.access_control.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Entity
@Table( name = "tb_applications" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "Applications_R", procedureName = "sp_Applications_R",parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Applications_C", procedureName = "sp_Applications_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtName", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTitle", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtAcronym", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Applications_U", procedureName = "sp_Applications_U", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtName", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTitle", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtAcronym", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Applications_D", procedureName = "sp_Applications_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "relUsrsByApp", procedureName = "sp_Report_USRBYAPP", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class )
    } )
public class TBApplication
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @Column( name = "name", nullable = false, length = 100 )
    @JsonProperty( "name" )
    private String name;

    @Column( name = "title", nullable = false, length = 100 )
    @JsonProperty( "title" )
    private String title;

    @Column( name = "acronym", nullable = false, length = 20 )
    @JsonProperty( "acronym" )
    private String acronym;

    @Column( name = "description", nullable = false, length = 250 )
    @JsonProperty( "description" )
    private String description;

    @Column( name = "created_at" )
    @JsonProperty( "created_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date createdAt;

    @Column( name = "updated_at" )
    @JsonProperty( "updated_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date updatedAt;

    @Column( name = "deleted_at" )
    @JsonProperty( "deleted_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date deletedAt;

    @OneToMany( mappedBy = "applications", cascade = CascadeType.ALL )
    private Set<TBUserApp> usersApps;

    @OneToMany( mappedBy = "applications", cascade = CascadeType.ALL )
    private Set<TBAppObj> appsobjs;
}