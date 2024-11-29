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
@Table( name = "tb_appsobjs" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "AppsObjs_R", procedureName = "sp_AppsObjs_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtAppObj", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "AppsObjs_C", procedureName = "sp_AppsObjs_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtObject", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "AppsObjs_D", procedureName = "sp_AppsObjs_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtAppObj", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "AppsObjs_Application", procedureName = "sp_AppsObjs_Application", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtApplication", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "AppsObjs_Object", procedureName = "sp_AppsObjs_Object", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtObject", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
public class TBAppObj
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
    @JoinColumn( name = "id_objects", nullable = false )
    private TBObject objects;

    @Column( name = "created_at" )
    @JsonProperty( "created_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date createdAt;

    @Column( name = "deleted_at" )
    @JsonProperty( "deleted_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date deletedAt;

    @OneToMany( mappedBy = "appsobjs", cascade = CascadeType.ALL )
    private Set<TBProfile> profiles;
}