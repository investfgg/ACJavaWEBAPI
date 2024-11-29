package com.access_control.service;

import com.access_control.entity.TBProfile;
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
public class TBProfileService
{
    @PersistenceContext
    private final EntityManager em;

    StoredProcedureQuery sqp;
    StoredProcedureQuery sqp2;

    String jsonProfile;
    String resultJSONProfile;
    String strReturn;

    public TBProfileService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllProfiles()
    {
        sqp = em.createNamedStoredProcedureQuery( "Profiles_R" );

        sqp.setParameter("pmtProfile", -1 );

        sqp.execute();

        return obtainResults_InJSON_TBProfile( sqp.getResultList() );
    }

    public String getProfile( long idProfile )
    {
        sqp = em.createNamedStoredProcedureQuery( "Profiles_R" );

        sqp.setParameter("pmtProfile", idProfile );

        sqp.execute();

        return obtainResults_InJSON_TBProfile( sqp.getResultList() );
    }

    public String createProfile( TBProfile tbProfile )
    {
        long idUserApp    = tbProfile.getUsersapps().getId();
        long idAppObj     = tbProfile.getAppsobjs().getId();
        long idUsTypePerm = tbProfile.getUstypeperms().getId();

        var pmtFields = validatorFieldsProfile( "UsersApps", idUserApp ).trim() +
                        validatorFieldsProfile( "AppsObjs", idAppObj ).trim() +
                        validatorFieldsProfile( "UsTypePerms", idUsTypePerm ).trim();

        if (!pmtFields.trim().isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the Profiles table! Broken rules: one of the fields (" +
                    pmtFields.trim().replace(")id_", "), id_") + ")" +
                    " contain some problems. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Profiles_C" );

        sqp.setParameter( "pmtUsersApps", idUserApp );
        sqp.setParameter( "pmtAppsObjs", idAppObj );
        sqp.setParameter( "pmtUsTypePerms", idAppObj );

        sqp.execute();

        return obtainResults_InJSON_TBProfile( sqp.getResultList() );
    }

    public String deleteProfile( long idProfile )
    {
        sqp = em.createNamedStoredProcedureQuery( "Profiles_D" );

        sqp.setParameter( "pmtProfile", idProfile );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "");
    }

    private String obtainResults_InJSON_TBProfile( List<Object[]> lstTBProfile )
    {

        if (lstTBProfile.toString().contains( "ortunately" ))
        {
            return lstTBProfile.toString().replace("[", "").replace("]", "");
        }

        jsonProfile       = "";
        resultJSONProfile = "";
        var mapperUserApp = new ObjectMapper().
                            enable( SerializationFeature.INDENT_OUTPUT ).
                            registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataProfile = new LinkedHashMap<>();
            Field[] flistProfile            = TBProfile.class.getDeclaredFields();
            var qtdeFields                  = flistProfile.length - 1;
            var x = 0;

            for (Object[] objProfile : lstTBProfile)
            {

                while (x <= qtdeFields)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataProfile.put( flistProfile[ x ].getName(), objProfile[ x ] );
                            break;
                        }

                        case 1:
                        {
                            dataProfile.put( "id_usersapps", fullUsersAppsIDJSON( (long) objProfile[ x ] ) );
                            break;
                        }

                        case 2:
                        {
                            dataProfile.put( "id_appsobjs", fullAppsObjsIDJSON( (long) objProfile[ x ] ) );
                            break;
                        }

                        case 3:
                        {
                            dataProfile.put( "id_ustypeperms", fullUsTypePermsIDJSON( (long) objProfile[ x ] ) );
                            break;
                        }

                        default:
                        {
                            dataProfile.put( flistProfile[ x ].getName(),
                                             objProfile[ x ] == null ? null : objProfile[ x ].toString() );
                            break;
                        }
                    }

                    x++;
                }

                jsonProfile = mapperUserApp.writerWithDefaultPrettyPrinter().writeValueAsString( dataProfile ) + ",";
                resultJSONProfile += jsonProfile;
                x = 0;
            }

            return resultJSONProfile.substring( 0, resultJSONProfile.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private Map<String, Object> fullUsersAppsIDJSON( long idUserApp )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "Profiles_UsersApps" );

        sqp2.setParameter( "pmtUserApp", idUserApp );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdUserApp      = sqp2.getResultList();
        Map<String, Object> dataUserApp = new LinkedHashMap<>();

        String[] flistUserApp = { "id", "id_application", "name_application", "id_usrsaccess", "username",
                                  "created_at", "deleted_at" };

        int qtdeFields = flistUserApp.length - 1;
        var y = 0;

        for (Object[] objUserApp : rsIdUserApp)
        {
            while (y <= qtdeFields)
            {
                if (y <= 4)
                {
                    dataUserApp.put( flistUserApp[ y ], objUserApp[ y ] );
                }

                else
                {

                    if (objUserApp[ y ] == null)
                    {
                        dataUserApp.put( flistUserApp[ y ], null );
                    }

                    else
                    {
                        dataUserApp.put( flistUserApp[ y ], objUserApp[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataUserApp;
    }

    private Map<String, Object> fullAppsObjsIDJSON( long idAppObj )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "Profiles_AppsObjs" );

        sqp2.setParameter( "pmtAppObj", idAppObj );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdAppObj      = sqp2.getResultList();
        Map<String, Object> dataAppObj = new LinkedHashMap<>();

        String[] flistAppObj = { "id", "id_application", "name_application", "id_object", "title_object",
                                 "created_at", "deleted_at" };

        int qtdeFields = flistAppObj.length - 1;
        var y = 0;

        for (Object[] objAppObj : rsIdAppObj)
        {

            while (y <= qtdeFields)
            {

                if (y <= 4)
                {
                    dataAppObj.put( flistAppObj[ y ], objAppObj[ y ] );
                }

                else
                {

                    if (objAppObj[ y ] == null)
                    {
                        dataAppObj.put( flistAppObj[ y ], null );
                    }

                    else
                    {
                        dataAppObj.put( flistAppObj[ y ], objAppObj[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataAppObj;
    }

    private Map<String, Object> fullUsTypePermsIDJSON( long idUsTypePerm )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "Profiles_UsTypePerms" );

        sqp2.setParameter( "pmtUsTypePerm", idUsTypePerm );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdUsTypePerm      = sqp2.getResultList();
        Map<String, Object> dataUsTypePerm = new LinkedHashMap<>();

        String[] flistUsTypePerm = { "id", "id_usertype", "title_usertype", "id_permission", "title_permission",
                                     "created_at", "deleted_at" };

        int qtdeFields = flistUsTypePerm.length - 1;
        var y = 0;

        for (Object[] objUsTypePerm : rsIdUsTypePerm)
        {
            while (y <= qtdeFields)
            {
                if (y <= 4)
                {
                    dataUsTypePerm.put( flistUsTypePerm[ y ], objUsTypePerm[ y ] );
                }

                else
                {

                    if (objUsTypePerm[ y ] == null)
                    {
                        dataUsTypePerm.put( flistUsTypePerm[ y ], null );
                    }

                    else
                    {
                        dataUsTypePerm.put( flistUsTypePerm[ y ], objUsTypePerm[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataUsTypePerm;
    }

    private String validatorFieldsProfile( String option, long id )
    {
        String strTentativas = "";

        if (id != -1)
        {
            strTentativas += validatorFieldsProfile_Tent( option, id );
            //strTentativas += (option.equals( "AppsObjs" ) && id == 0) ? "id_appsobjs (no code)" : "";
            //strTentativas += (option.equals( "UsTypePerms" ) && id == 0) ? "id_ustypeperms (no code)" : "";

            strTentativas += (option.equals( "UsersApps" ) &&
                              id != 0 && idExistinTBUsersApps( id ) == 0) ? "id_usersapps (not exists), " : "";

            strTentativas += (option.equals( "AppsObjs" ) &&
                              id != 0 && idExistinTBAppsObjs( id ) == 0) ? "id_appsobjs (not exists), " : "";

            strTentativas += (option.equals( "UsTypePerms" ) &&
                              id != 0 && idExistinTBUsTypePerms( id ) == 0) ? "id_ustypeperms (not exists), " : "";
        }

        return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private String validatorFieldsProfile_Tent( String option, long id )
    {
        strReturn = (id == 0) ? "id_" + option.toLowerCase() + " (no code)" : "";

        return strReturn;
    }

    private long idExistinTBUsersApps( long idUsersApps )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "Profiles_UsersApps" );

        sqp2.setParameter( "pmtUserApp", idUsersApps );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */

        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }

    private long idExistinTBAppsObjs( long idAppsObjs )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "Profiles_AppsObjs" );

        sqp2.setParameter( "pmtAppObj", idAppsObjs );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */

        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }

    private long idExistinTBUsTypePerms( long idUsTypePerms )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "Profiles_UsTypePerms" );

        sqp2.setParameter( "pmtUsTypePerm", idUsTypePerms );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */

        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }
}