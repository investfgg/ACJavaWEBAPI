package com.access_control.service;

import com.access_control.entity.TBUserApp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.lang.reflect.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.procedure.internal.ProcedureCallImpl;

import org.springframework.stereotype.Service;

@Service
public class TBUserAppService
{
    @PersistenceContext
    private final EntityManager em;

    StoredProcedureQuery sqp;
    StoredProcedureQuery sqp2;

    String jsonUserApp;
    String resultJSONUserApp;

    public TBUserAppService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllUserApps()
    {
        sqp = em.createNamedStoredProcedureQuery( "UsersApps_R" );

        sqp.setParameter("pmtUserApp", -1 );

        sqp.execute();

        return obtainResults_InJSON_TBUserApp( sqp.getResultList() );
    }

    public String getUserApp( long idUserApp )
    {
        sqp = em.createNamedStoredProcedureQuery( "UsersApps_R" );

        sqp.setParameter("pmtUserApp", idUserApp );

        sqp.execute();

        return obtainResults_InJSON_TBUserApp( sqp.getResultList() );
    }

    public String createUserApp( TBUserApp tbUserApp )
    {
        long idApp    = tbUserApp.getApplications().getId();
        long idUsrAcc = tbUserApp.getUsrsAccesses().getId();

        var pmtFields = validatorFieldsUserApp( "App", idApp ).trim() +
                        validatorFieldsUserApp( "UsA", idUsrAcc ).trim();

        if (!pmtFields.trim().isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                   "in the UsersApps table! Broken rules: one of the fields (" +
                   pmtFields.trim().replace(")id_", "), id_") + ")" +
                   " contain some problems. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "UsersApps_C" );

        sqp.setParameter( "pmtApplication", idApp );
        sqp.setParameter( "pmtUsrAccess", idUsrAcc );

        sqp.execute();

        return obtainResults_InJSON_TBUserApp( sqp.getResultList() );
    }

    public String deleteUserApp( long idUserApp )
    {
        sqp = em.createNamedStoredProcedureQuery( "UsersApps_D" );

        sqp.setParameter( "pmtUserApp", idUserApp );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "");
    }

    private String obtainResults_InJSON_TBUserApp( List<Object[]> lstTBUserApp )
    {

        if (lstTBUserApp.toString().contains( "ortunately" ))
        {
            return lstTBUserApp.toString().replace("[", "").replace("]", "");
        }

        jsonUserApp       = "";
        resultJSONUserApp = "";
        var mapperUserApp = new ObjectMapper().
                            enable( SerializationFeature.INDENT_OUTPUT ).
                            registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataUserApp = new LinkedHashMap<>();
            Field[] flistUserApp            = TBUserApp.class.getDeclaredFields();
            // Diminui menos 1, e menos 1 (campo "profiles", por ser 'OneToMany').
            int qtdeFields                  = flistUserApp.length - 2;

            var x = 0;

            for (Object[] resultUserApp : lstTBUserApp)
            {

                while (x <= qtdeFields)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataUserApp.put( flistUserApp[ x ].getName(), resultUserApp[ x ] );
                            break;
                        }

                        case 1:
                        {
                            dataUserApp.put( "id_applications", fullApplicationIDJSON( (long) resultUserApp[ x ] ) );
                            break;
                        }

                        case 2:
                        {
                            dataUserApp.put( "id_usrsaccess", fullUsrAccessIDJSON( (long) resultUserApp[ x ] ) );
                            break;
                        }

                        default:
                        {
                            dataUserApp.put( flistUserApp[ x ].getName(),
                                             resultUserApp[ x ] == null ? null : resultUserApp[ x ].toString() );
                            break;
                        }
                    }

                    x++;
                }

                jsonUserApp = mapperUserApp.writerWithDefaultPrettyPrinter().writeValueAsString( dataUserApp ) + ",";
                resultJSONUserApp += jsonUserApp;
                x = 0;
            }

            return resultJSONUserApp.substring( 0, resultJSONUserApp.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private Map<String, Object> fullApplicationIDJSON( long idApplication )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsersApps_Application" );

        sqp2.setParameter( "pmtApplication", idApplication );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdApplication      = sqp2.getResultList();
        Map<String, Object> dataApplication = new LinkedHashMap<>();

        String[] flistApplication = { "id", "name", "title", "acronym", "description",
                                      "created_at", "updated_at", "deleted_at" };

        int qtdeFields = flistApplication.length - 1;
        var y = 0;

        for (Object[] resultIDApplication : rsIdApplication)
        {

            while (y <= qtdeFields)
            {

                if (y <= 4)
                {
                    dataApplication.put( flistApplication[ y ], resultIDApplication[ y ] );
                }

                else
                {

                    if (resultIDApplication[ y ] == null)
                    {
                        dataApplication.put( flistApplication[ y ], null );
                    }

                    else
                    {
                        dataApplication.put( flistApplication[ y ], resultIDApplication[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataApplication;
    }

    private Map<String, Object> fullUsrAccessIDJSON( long idUsrAccess )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsersApps_UsrAccess" );

        sqp2.setParameter( "pmtUsrAccess", idUsrAccess );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdUsrAccess      = sqp2.getResultList();
        Map<String, Object> dataUsrAccess = new LinkedHashMap<>();

        String[] flistUsrAccess = { "id", "username", "password", "tip",
                                    "created_at", "updated_at", "deleted_at" };

        int qtdeFields = flistUsrAccess.length - 1;
        var y = 0;

        for (Object[] objUsrAccess : rsIdUsrAccess)
        {

            while (y <= qtdeFields)
            {

                if (y <= 3)
                {
                    dataUsrAccess.put( flistUsrAccess[ y ], objUsrAccess[ y ] );
                }

                else
                {

                    if (objUsrAccess[ y ] == null)
                    {
                        dataUsrAccess.put( flistUsrAccess[ y ], null );
                    }

                    else
                    {
                        dataUsrAccess.put( flistUsrAccess[ y ], objUsrAccess[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataUsrAccess;
    }

    private String validatorFieldsUserApp( String option, long id )
    {
        String strTentativas = "";

        if (id != -1)
        {
            strTentativas += (option.equals( "App" ) && id == 0) ? "id_applications (no code)" : "";
            strTentativas += (option.equals( "UsA" ) && id == 0) ? "id_usrsAccess (no code)" : "";

            strTentativas += (option.equals( "App" ) && id != 0 && idExistInTBApplication( id ) == 0) ?
                    "id_applications (not exists), " : "";

            strTentativas += (option.equals( "UsA" ) && id != 0 && idExistInTBUsrAccess( id ) == 0) ?
                    "id_usrsAccess (not exists), " : "";
        }

        return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private long idExistInTBApplication( long idApplication ) {
        sqp2 = em.createNamedStoredProcedureQuery( "UsersApps_Application" );

        sqp2.setParameter( "pmtApplication", idApplication );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */

        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }

    private long idExistInTBUsrAccess( long idUsrAccess ) {
        sqp2 = em.createNamedStoredProcedureQuery( "UsersApps_UsrAccess" );

        sqp2.setParameter( "pmtUsrAccess", idUsrAccess );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */

        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }
}
