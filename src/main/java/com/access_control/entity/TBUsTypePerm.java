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
@Table( name = "tb_ustypeperms" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "UsTypePerms_R", procedureName = "sp_UsTypePerms_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsTypePerm", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "UsTypePerms_C", procedureName = "sp_UsTypePerms_C", parameters =
    {
            @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserType", type = String.class ),
            @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPermission", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "UsTypePerms_D", procedureName = "sp_UsTypePerms_D", parameters =
    {
            @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsTypePerm", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "UsTypePerms_UserType", procedureName = "sp_UsTypePerms_UserType", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserType", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "UsTypePerms_Permission", procedureName = "sp_UsTypePerms_Permission", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPermission", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
public class TBUsTypePerm
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "id_usertypes", nullable = false )
    private TBUserType usertypes;

    @ManyToOne
    @JoinColumn( name = "id_permissions", nullable = false )
    private TBPermission permissions;

    @Column( name = "created_at" )
    @JsonProperty( "created_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date createdAt;

    @Column( name = "deleted_at" )
    @JsonProperty( "deleted_at" )
    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME, fallbackPatterns = { "yyyy-mm-dd hh:mm:ss" } )
    private Date deletedAt;

    @OneToMany( mappedBy = "ustypeperms", cascade = CascadeType.ALL )
    private Set<TBProfile> profiles;
}