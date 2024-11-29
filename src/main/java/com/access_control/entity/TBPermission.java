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
@Table( name = "tb_permissions" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "Permissions_R", procedureName = "sp_Permissions_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPermission", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Permissions_C", procedureName = "sp_Permissions_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTitle", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Permissions_U", procedureName = "sp_Permissions_U", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPermission", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTitle", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Permissions_D", procedureName = "sp_Permissions_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPermission", type = long.class )
    } )
public class TBPermission
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @Column( name = "title", nullable = false, length = 100 )
    @JsonProperty( "title" )
    private String title;

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

    @OneToMany( mappedBy = "permissions", cascade = CascadeType.ALL )
    private Set<TBUsTypePerm> ustypeperms;
}