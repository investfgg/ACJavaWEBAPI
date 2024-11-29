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
@Table( name = "tb_usersapps" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "UsersApps_R", procedureName = "sp_UsersApps_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserApp", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "UsersApps_C", procedureName = "sp_UsersApps_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsrAccess", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "UsersApps_D", procedureName = "sp_UsersApps_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserApp", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "UsersApps_Application", procedureName = "sp_UsersApps_Application", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "UsersApps_UsrAccess", procedureName = "sp_UsersApps_UsrAccess", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsrAccess", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
public class TBUserApp
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "id_applications", nullable = false )
    private TBApplication applications;

    @ManyToOne
    @JoinColumn( name = "id_usrsaccess", nullable = false )
    private TBUsrAccess usrsAccesses;

    @Column( name = "created_at" )
    @JsonProperty( "created_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date createdAt;

    @Column( name = "deleted_at" )
    @JsonProperty( "deleted_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date deletedAt;

    @OneToMany( mappedBy = "usersapps", cascade = CascadeType.ALL )
    private Set<TBProfile> profiles;
}