package com.access_control.service;

import com.access_control.entity.TBAppObj;

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
public class TBAppObjService
{
    @PersistenceContext
    private final EntityManager em;

    StoredProcedureQuery sqp;
    StoredProcedureQuery sqp2;

    String strTentativas;
    String jsonAppObj;
    String resultJSONAppObj;

    public TBAppObjService(EntityManager em)
    {
        this.em = em;
    }

    public String getAllAppObjs()
    {
        sqp = em.createNamedStoredProcedureQuery( "AppsObjs_R" );

        sqp.setParameter("pmtAppObj", -1 ); // OBSERVAÇÃO: O valor '-1' traz todos os objetos.
        sqp.execute();

        return obtainResults_InJSON_TBAppObj( sqp.getResultList() );
    }

    public String getAppObj( long idAppObj )
    {
        sqp = em.createNamedStoredProcedureQuery( "AppsObjs_R" );

        sqp.setParameter("pmtAppObj", idAppObj );
        sqp.execute();

        return obtainResults_InJSON_TBAppObj( sqp.getResultList() );
    }

    public String createAppObj( TBAppObj tbAppObj )
    {
        long idApplication = tbAppObj.getApplications().getId();
        long idObject      = tbAppObj.getObjects().getId();

        var pmtFields = validatorFieldsAppObj( "App", idApplication );
        pmtFields += validatorFieldsAppObj( "Obj", idObject );
        pmtFields = (!pmtFields.isEmpty()) ? pmtFields.substring( 0, pmtFields.length() - 2 ) : "";

        if (!pmtFields.trim().isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the UsersApps table! Broken rules: one of the fields (" +
                    pmtFields + ") contain some problems. Please, check it again.";
                    //pmtFields.trim().replace("), id_", "), id_") + ")" +
        }

        sqp = em.createNamedStoredProcedureQuery( "AppsObjs_C" );

        sqp.setParameter( "pmtApplication", idApplication );
        sqp.setParameter( "pmtObject", idObject );

        sqp.execute();

        return obtainResults_InJSON_TBAppObj( sqp.getResultList() );
    }

    // Delete
    public String deleteAppObj( long idAppObj )
    {
        sqp = em.createNamedStoredProcedureQuery( "AppsObjs_D" );

        sqp.setParameter( "pmtAppObj", idAppObj );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    private String obtainResults_InJSON_TBAppObj( List<Object[]> lstTBAppObj )
    {

        if (lstTBAppObj.toString().contains( "ortunately" ))
        {
            return lstTBAppObj.toString().replace("[", "").replace("]", "");
        }

        jsonAppObj        = "";
        resultJSONAppObj  = "";
        var mapperUserApp = new ObjectMapper().
                            enable( SerializationFeature.INDENT_OUTPUT ).
                            registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataAppObj = new LinkedHashMap<>();
            Field[] flistAppObj            = TBAppObj.class.getDeclaredFields();
            var qtdFields                  = flistAppObj.length - 2;
            var x = 0;

            for (Object[] resultAppObj : lstTBAppObj)
            {

                while (x <= qtdFields)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataAppObj.put( flistAppObj[ x ].getName(), resultAppObj[ x ] );
                            break;
                        }

                        case 1:
                        {
                            dataAppObj.put( "id_applications", fullApplicationIDJSON( (long) resultAppObj[ x ] ) );
                            break;
                        }

                        case 2:
                        {
                            dataAppObj.put( "id_objects", fullObjectIDJSON( (long) resultAppObj[ x ] ) );
                            break;
                        }

                        default:
                        {
                            dataAppObj.put( flistAppObj[ x ].getName(),
                                            resultAppObj[ x ] == null ? null : resultAppObj[ x ].toString() );
                            break;
                        }
                    }

                    x++;
                }

                jsonAppObj = mapperUserApp.writerWithDefaultPrettyPrinter().writeValueAsString( dataAppObj ) + ",";
                resultJSONAppObj += jsonAppObj;
                x = 0;
            }

            return resultJSONAppObj.substring( 0, resultJSONAppObj.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private Map<String, Object> fullApplicationIDJSON( long idApplication )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "AppsObjs_Application" );

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

    private Map<String, Object> fullObjectIDJSON( long idObject )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "AppsObjs_Object" );

        sqp2.setParameter( "pmtObject", idObject );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> rsIdObject      = sqp2.getResultList();
        Map<String, Object> dataObject = new LinkedHashMap<>();

        String[] flistObject = { "id", "title", "description", "created_at", "updated_at", "deleted_at" };
        int qtdeFields       = flistObject.length - 1;
        var y = 0;

        for (Object[] resultIDObject : rsIdObject)
        {

            while (y <= qtdeFields)
            {

                if (y <= 2)
                {
                    dataObject.put( flistObject[ y ], resultIDObject[ y ] );
                }

                else
                {

                    if (resultIDObject[ y ] == null)
                    {
                        dataObject.put( flistObject[ y ], null );
                    }

                    else
                    {
                        dataObject.put( flistObject[ y ], resultIDObject[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataObject;
    }

    private String validatorFieldsAppObj( String option, long id )
    {
        strTentativas = "";

        if (id != -1)
        {
            strTentativas += (option.equals( "App" ) && id == 0) ? "applications - no ID, " : "";
            strTentativas += (option.equals( "Obj" ) && id == 0) ? "objects - no ID, " : "";

            strTentativas += (option.equals( "App" ) && id != 0 && idExistinTBApplication( id ) == 0) ?
                             "applications - ID not found, " : "";

            strTentativas += (option.equals( "Obj" ) && id != 0 && idExistinTBObject( id ) == 0) ?
                             "objects - ID not found, " : "";
        }

        return strTentativas;
        //return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private long idExistinTBApplication( long idApplication )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "AppsObjs_Application" );

        sqp2.setParameter( "pmtApplication", idApplication );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */
        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }

    private long idExistinTBObject( long idObject )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "AppsObjs_Object" );

        sqp2.setParameter( "pmtObject", idObject );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */
        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }
}