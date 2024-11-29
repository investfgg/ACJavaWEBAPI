package com.access_control.service;

import com.access_control.entity.TBApplication;

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

import org.springframework.stereotype.Service;

@Service
public class TBApplicationService
{
    @PersistenceContext
    private final EntityManager em;
    StoredProcedureQuery sqp;

    String jsonApplication;
    String resultJSONApplication;

    String strReasonCreate = "** REASON: Unfortunately, it was not possible to insert the new ID " +
                             "in the Applications table! Broken rules: one of the fields (";

    String strReasonUpdate = "** REASON: Unfortunately, ";

    public TBApplicationService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllApplications()
    {
        sqp = em.createNamedStoredProcedureQuery( "Applications_R" );

        sqp.setParameter("pmtApplication", -1 );
        sqp.execute();

        return obtainResults_InJSON_TBApplication( sqp.getResultList() );
    }

    public String getApplication( long idApplication )
    {
        sqp = em.createNamedStoredProcedureQuery( "Applications_R" );

        sqp.setParameter("pmtApplication", idApplication );
        sqp.execute();

        return obtainResults_InJSON_TBApplication( sqp.getResultList() );
    }

    public String createApplication( TBApplication tbApplication )
    {

        if (!validatorFieldsApp( tbApplication, 1 ).isEmpty())
        {
            return strReasonCreate + validatorFieldsApp( tbApplication, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsApp( tbApplication, 2 ).isEmpty())
        {
            return strReasonCreate + validatorFieldsApp( tbApplication, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Applications_C" );

        sqp.setParameter("pmtName", tbApplication.getName() );
        sqp.setParameter("pmtTitle", tbApplication.getTitle() );
        sqp.setParameter("pmtAcronym", tbApplication.getAcronym() );
        sqp.setParameter("pmtDescription", tbApplication.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBApplication( sqp.getResultList() );
    }

    public String updateApplication( long idApplication, TBApplication tbApplication )
    {

        if (tbApplication.getId() == null)
        {
            return strReasonUpdate + "the field 'ID' in the JSON format is not present in Application set. "+
                   "Put the field 'ID' and try it.";
        }

        if (tbApplication.getId() != idApplication)
        {
            return strReasonUpdate + "the ID (" + idApplication + ") whose parameter with described value "+
                   "is different than the ID filled (" + tbApplication.getId() +
                   ") in set of Application by the JSON format. Fill the same correct ID in both.";
        }

        if (!validatorFieldsApp( tbApplication, 1 ).isEmpty())
        {
            return strReasonUpdate + "it was not possible to update the actual ID (" + idApplication +
                   ") in the Applications table! Broken rules: one of the fields (" +
                   validatorFieldsApp( tbApplication, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsApp( tbApplication, 2 ).isEmpty())
        {
            return strReasonUpdate + "it was not possible to update the actual ID " +
                   "in the Applications table! Broken rules: one of the fields (" +
                   validatorFieldsApp( tbApplication, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Applications_U" );

        sqp.setParameter("pmtApplication", idApplication );
        sqp.setParameter("pmtName", tbApplication.getName() );
        sqp.setParameter("pmtTitle", tbApplication.getTitle() );
        sqp.setParameter("pmtAcronym", tbApplication.getAcronym() );
        sqp.setParameter("pmtDescription", tbApplication.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBApplication( sqp.getResultList() );
    }

    public String deleteApplication( long idApplication )
    {
        sqp = em.createNamedStoredProcedureQuery( "Applications_D" );

        sqp.setParameter("pmtApplication", idApplication );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "");
    }

    public String report_UsersApps_UsrByApp()
    {
        sqp = em.createNamedStoredProcedureQuery( "relUsrsByApp" );

        sqp.setParameter("pmtApplication", -1 );

        sqp.execute();

        return obtainResults_InJSON_TBUserApps( sqp.getResultList() );
    }

    public String report_UsersApps_IdUsrByApp( long idApp )
    {
        sqp = em.createNamedStoredProcedureQuery( "relUsrsByApp" );

        sqp.setParameter("pmtApplication", idApp );

        sqp.execute();

        return obtainResults_InJSON_TBUserApps( sqp.getResultList() );
    }

    private String obtainResults_InJSON_TBApplication( List<Object[]> listApplications )
    {
        jsonApplication       = "";
        resultJSONApplication = "";

        if (listApplications.toString().contains( "ortunately" ))
        {
            // Eliminar colchetes para não gerar confusão com formato JSON.
            // Resultado recém-sa´dio diretamente no Stored Procedure.
            return listApplications.toString().replace("[", "").replace("]", "");
        }

        var mapperApplication = new ObjectMapper().
                                enable( SerializationFeature.INDENT_OUTPUT ).
                                registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> mapApplication = new LinkedHashMap<>();
            Field[] fieldsApplication          = TBApplication.class.getDeclaredFields();
            var qtdeFields                     = TBApplication.class.getDeclaredFields().length - 3;
            var x = 0;

            for (Object[] objApplication : listApplications)
            {

                while (x <= qtdeFields)
                {

                    if (x == 0)
                    {
                        mapApplication.put( fieldsApplication[ x ].getName(), objApplication[ x ] );
                    }

                    else if (x <= 4)
                    {
                        mapApplication.put( fieldsApplication[ x ].getName(), objApplication[ x ].toString() );
                    }

                    else
                    {
                        mapApplication.put( fieldsApplication[ x ].getName(),
                                                objApplication[ x ] == null ? null : objApplication[ x ].toString() );
                    }

                    x++;
                }

                jsonApplication = mapperApplication.writerWithDefaultPrettyPrinter().
                                  writeValueAsString( mapApplication ) + ",";

                resultJSONApplication += jsonApplication;
                x = 0;
            }

            return resultJSONApplication.substring( 0, resultJSONApplication.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private String validatorFieldsApp( TBApplication tbApplication, int option )
    {
        String strTentativas = "";

        if (option == 1)
        {
            strTentativas +=
                charactLimitApp( "Name", tbApplication.getName().trim(), 100, option ) +
                charactLimitApp( "Title", tbApplication.getTitle().trim(), 100, option ) +
                charactLimitApp( "Acronym", tbApplication.getAcronym().trim(), 20, option ) +
                charactLimitApp( "Description", tbApplication.getDescription().trim(), 250, option );
        }

        if (option == 2)
        {
            strTentativas +=
                charactLimitApp( "Name", tbApplication.getName().trim(), 3, option ) +
                charactLimitApp( "Title", tbApplication.getTitle().trim(), 3, option ) +
                charactLimitApp( "Acronym", tbApplication.getAcronym().trim(), 3, option ) +
                charactLimitApp( "Description", tbApplication.getDescription().trim(), 3, option );
        }

        return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private String charactLimitApp(String strField, String strValueField, int lengthField, int option )
    {
        var strReturn = "";

        if (option == 1)
        {
            strReturn = (strValueField.length() > lengthField) ?
                        strField + "(" + strValueField.length() + " of " + lengthField + "), " : "";
        }

        if (option == 2)
        {
            strReturn = (strValueField.isEmpty() || strValueField.length() <= lengthField) ? strField + ", " : "";
        }

        return strReturn;
    }

    private String obtainResults_InJSON_TBUserApps( List<Object[]> lstApplications )
    {

        if (lstApplications.toString().contains( "ortunately" ))
        {
            // Eliminar colchetes para não gerar confusão com formato JSON.
            // Resultado recém-sa´dio diretamente no Stored Procedure.
            return lstApplications.toString().replace("[", "").replace("]", "");
        }

        StringBuilder sbJoinApplication = new StringBuilder();
        var mapperApplication = new ObjectMapper().
                                enable( SerializationFeature.INDENT_OUTPUT ).
                                registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> mapApplications = new LinkedHashMap<>();

            for (Object[] objApplication : lstApplications)
            {
                mapApplications.put( "usersAppsID",       objApplication[ 0 ] );
                mapApplications.put( "applicationID",     objApplication[ 1 ] );
                mapApplications.put( "applicationName",   objApplication[ 2 ].toString() );
                mapApplications.put( "applicationTitle",  objApplication[ 3 ].toString() );
                mapApplications.put( "usrAccessID",       objApplication[ 4 ] );
                mapApplications.put( "usrAccessUserName", objApplication[ 5 ].toString() );
                mapApplications.put( "userName",          objApplication[ 6 ].toString() );
                mapApplications.put( "userEmail",         objApplication[ 7 ].toString() );

                sbJoinApplication.append( mapperApplication.writerWithDefaultPrettyPrinter().
                                          writeValueAsString( mapApplications ) + "," );
            }

            //return json.toString().substring( 0, json.length() - 1 );
            return sbJoinApplication.substring( 0, sbJoinApplication.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }
}