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
@Table( name = "tb_users" )
@Setter
@Getter
@Data
@NamedStoredProcedureQuery( name = "Users_R", procedureName = "sp_Users_R", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "Users_I", procedureName = "sp_Users_C", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtName", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtEmail", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Users_U", procedureName = "sp_Users_U", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtName", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtEmail", type = String.class ),
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtDescription", type = String.class )
    } )
@NamedStoredProcedureQuery( name = "Users_D", procedureName = "sp_Users_D", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "relUsrAccByUser", procedureName = "sp_Report_USRACCBYUSER", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class )
    } )
@NamedStoredProcedureQuery( name = "fullUsrAccess_us", procedureName = "sp_User_FullUsrAccess", parameters =
    {
        @StoredProcedureParameter( mode = ParameterMode.IN, name = "pmtUser", type = long.class )
    } )
public class TBUser {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    @JsonProperty( "id" )
    private Long id;

    @Column( name = "name", nullable = false, length = 100 )
    @JsonProperty( "name" )
    private String name;

    @Column( name = "email", nullable = false, length = 100 )
    @JsonProperty( "email" )
    private String email;

    @Column( name = "description", nullable = false, length = 250 )
    @JsonProperty( "description" )
    private String description;

    @OneToMany( mappedBy = "users", cascade = CascadeType.ALL )
    private Set<TBUsrAccess> usrsAccesses;

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