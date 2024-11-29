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
@Table( name = "tb_usraccess" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "UsrAccess_R", procedureName = "sp_UsrAccess_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsrAccess", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "UsrAccess_C", procedureName = "sp_UsrAccess_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserName", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPassword", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTip", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "UsrAccess_U", procedureName = "sp_UsrAccess_U", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsrAccess", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUserName", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtPassword", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtTip", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "UsrAccess_D", procedureName = "sp_UsrAccess_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUsrAccess", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "relUsrAccess", procedureName = "sp_Report_UsrAccess" ) // No parameters
@NamedStoredProcedureQuery( name = "UsrAccess_User", procedureName = "sp_UsrAccess_User", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtOperation", type = String.class )
    } )
//@NamedQuery( name = "UsrAccess.findAllWithoutDeletedAt", query = "select * from tb_usraccess where deletedAt is NULL" )
//@NamedQuery( name = "UsrAccess.findByIDWithoutDeletedAt", query = "select * from tb_usraccess where ID = ?1 deletedAt is NULL" )
public class TBUsrAccess
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @Column( name = "username", nullable = false, length = 100 )
    @JsonProperty( "username" )
    private String userName;

    @Column( name = "password", nullable = false, length = 100 )
    @JsonProperty( "password" )
    private String password;

    @Column( name = "tip", nullable = false, length = 100 )
    @JsonProperty( "tip" )
    private String tip;

    @ManyToOne
    @JoinColumn( name = "id_users", nullable = false )
    private TBUser users;

    @OneToMany( mappedBy = "usrsAccesses", cascade = CascadeType.ALL )
    private Set<TBUserApp> usersApps;

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
}