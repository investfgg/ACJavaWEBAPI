package com.access_control.service;

import com.access_control.entity.TBUsTypePerm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TBUsTypePermService
{
    @PersistenceContext
    private final EntityManager em;
    StoredProcedureQuery sqp;
    StoredProcedureQuery sqp2;

    String strTentativas;
    String jsonUsTypePerm;
    String resultJSONUsTypePerm;

    public TBUsTypePermService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllUsTypePerms()
    {
        sqp = em.createNamedStoredProcedureQuery( "UsTypePerms_R" );

        sqp.setParameter("pmtUsTypePerm", -1 ); // OBSERVAÇÃO: O valor '-1' traz todos os objetos.
        sqp.execute();

        return obtainResults_InJSON_TBUsTypePerm( sqp.getResultList() );
    }

    public String getUsTypePerms( long idUsTypePerm )
    {
        sqp = em.createNamedStoredProcedureQuery( "UsTypePerms_R" );

        sqp.setParameter("pmtUsTypePerm", idUsTypePerm ); // OBSERVAÇÃO: O valor '-1' traz todos os objetos.
        sqp.execute();

        return obtainResults_InJSON_TBUsTypePerm( sqp.getResultList() );
    }

    public String createUsTypePerms( TBUsTypePerm tbUsTypePerm )
    {
        long idUserType   = tbUsTypePerm.getUsertypes().getId();
        long idPermission = tbUsTypePerm.getPermissions().getId();

        var pmtFields = validatorFieldsUsTypePerm( "UserType", idUserType );
        pmtFields += validatorFieldsUsTypePerm( "Permission", idPermission );
        pmtFields = (!pmtFields.isEmpty()) ? pmtFields.substring( 0, pmtFields.length() - 2 ) : "";

        if (!pmtFields.trim().isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the UsersApps table! Broken rules: one of the fields (" +
                    pmtFields + ") contain some problems. Please, check it again.";
            //pmtFields.trim().replace("), id_", "), id_") + ")" +
        }

        sqp = em.createNamedStoredProcedureQuery( "UsTypePerms_C" );

        sqp.setParameter( "pmtUserType", idUserType );
        sqp.setParameter( "pmtPermission", idPermission );

        sqp.execute();

        return obtainResults_InJSON_TBUsTypePerm( sqp.getResultList() );
    }

    public String deleteUsTypePerms( long idUsTypePerm )
    {
        sqp = em.createNamedStoredProcedureQuery( "UsTypePerms_D" );

        sqp.setParameter( "pmtUsTypePerm", idUsTypePerm );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    private String obtainResults_InJSON_TBUsTypePerm( List<Object[]> lstTBUsTypePerm )
    {

        if (lstTBUsTypePerm.toString().contains( "ortunately" ))
        {
            return lstTBUsTypePerm.toString().replace("[", "").replace("]", "");
        }

        jsonUsTypePerm       = "";
        resultJSONUsTypePerm = "";
        var mapperUserApp    = new ObjectMapper().
                               enable( SerializationFeature.INDENT_OUTPUT ).
                               registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataUsTypePerm = new LinkedHashMap<>();
            Field[] flistUsTypePerm            = TBUsTypePerm.class.getDeclaredFields();
            var qtdeFields                     = flistUsTypePerm.length - 2;
            var x = 0;

            for (Object[] resultUsTypePerm : lstTBUsTypePerm)
            {

                while (x <= qtdeFields)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataUsTypePerm.put( flistUsTypePerm[ x ].getName(), resultUsTypePerm[ x ] );
                            break;
                        }

                        case 1:
                        {
                            dataUsTypePerm.put( "id_usertypes",
                                                fullUserTypeIDJSON( (long) resultUsTypePerm[ x ] ) );
                            break;
                        }

                        case 2:
                        {
                            dataUsTypePerm.put( "id_permissions",
                                                fullPermissionIDJSON( (long) resultUsTypePerm[ x ] ) );
                            break;
                        }

                        default:
                        {
                            dataUsTypePerm.put( flistUsTypePerm[ x ].getName(),
                                                resultUsTypePerm[ x ] == null ?
                                                        null : resultUsTypePerm[ x ].toString() );
                            break;
                        }
                    }

                    x++;
                }

                jsonUsTypePerm = mapperUserApp.writerWithDefaultPrettyPrinter().
                                 writeValueAsString( dataUsTypePerm ) + ",";

                resultJSONUsTypePerm += jsonUsTypePerm;
                x = 0;
            }

            return resultJSONUsTypePerm.substring( 0, resultJSONUsTypePerm.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private Map<String, Object> fullUserTypeIDJSON( long idUserType )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsTypePerms_UserType" );

        sqp2.setParameter( "pmtUserType", idUserType );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdUserType      = sqp2.getResultList();
        Map<String, Object> dataUserType = new LinkedHashMap<>();

        String[] flistUserType = { "id", "title", "description", "created_at", "updated_at", "deleted_at" };

        int qtdeFields = flistUserType.length - 1;
        var y = 0;

        for (Object[] resultIDUserType : rsIdUserType)
        {

            while (y <= qtdeFields)
            {

                if (y <= 2)
                {
                    dataUserType.put( flistUserType[ y ], resultIDUserType[ y ] );
                }

                else
                {

                    if (resultIDUserType[ y ] == null)
                    {
                        dataUserType.put( flistUserType[ y ], null );
                    }

                    else
                    {
                        dataUserType.put( flistUserType[ y ], resultIDUserType[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataUserType;
    }

    private Map<String, Object> fullPermissionIDJSON( long idPermission )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsTypePerms_Permission" );

        sqp2.setParameter( "pmtPermission", idPermission );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdPermission      = sqp2.getResultList();
        Map<String, Object> dataPermission = new LinkedHashMap<>();

        String[] flistPermission = { "id", "title", "description", "created_at", "updated_at", "deleted_at" };
        int qtdeFields           = flistPermission.length - 1;
        var y = 0;

        for (Object[] resultIDPermission : rsIdPermission)
        {

            while (y <= qtdeFields)
            {

                if (y <= 2)
                {
                    dataPermission.put( flistPermission[ y ], resultIDPermission[ y ] );
                }

                else
                {

                    if (resultIDPermission[ y ] == null)
                    {
                        dataPermission.put( flistPermission[ y ], null );
                    }

                    else
                    {
                        dataPermission.put( flistPermission[ y ], resultIDPermission[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataPermission;
    }

    private String validatorFieldsUsTypePerm( String option, long id )
    {
        strTentativas = "";

        if (id != -1)
        {
            strTentativas += (option.equals( "UserType" ) && id == 0) ? "usertypes - no ID, " : "";
            strTentativas += (option.equals( "Permission" ) && id == 0) ? "permissions - no ID, " : "";

            strTentativas += (option.equals( "UserType" ) &&
                              id != 0 && idExistinTBUserTypes( id ) == 0) ? "usertypes - ID not found, " : "";

            strTentativas += (option.equals( "Permission" ) &&
                              id != 0 && idExistinTBPermissions( id ) == 0) ? "permissions - ID not found, " : "";
        }

        return strTentativas;
        //return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private long idExistinTBUserTypes( long idUserType )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsTypePerms_UserType" );

        sqp2.setParameter( "pmtUserType", idUserType );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */
        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }

    private long idExistinTBPermissions( long idPermission )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsTypePerms_Permission" );

        sqp2.setParameter( "pmtPermission", idPermission );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */
        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }
}