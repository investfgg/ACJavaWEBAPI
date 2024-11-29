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
@Table( name = "tb_objects" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "Objects_R", procedureName = "sp_Objects_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtObject", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Objects_C", procedureName = "sp_Objects_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTitle", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Objects_U", procedureName = "sp_Objects_U", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtObject", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTitle", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Objects_D", procedureName = "sp_Objects_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtObject", type = long.class )
    } )
public class TBObject
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

    @OneToMany( mappedBy = "objects", cascade = CascadeType.ALL )
    private Set<TBAppObj> appsobjs;
}