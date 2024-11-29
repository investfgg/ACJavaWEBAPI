package com.access_control.service;

import com.access_control.entity.TBObject;

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
public class TBObjectService
{
    @PersistenceContext
    private final EntityManager em;
    StoredProcedureQuery sqp;

    String jsonObj;
    String resultJSONObj;

    public TBObjectService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllObjects()
    {
        sqp = em.createNamedStoredProcedureQuery( "Objects_R" );

        sqp.setParameter("pmtObject", -1 ); // OBSERVAÇÃO: O valor '-1' traz todos os objetos.
        sqp.execute();

        return obtainResults_InJSON_TBObject( sqp.getResultList() );
    }

    public String getObject( long idObject )
    {
        sqp = em.createNamedStoredProcedureQuery( "Objects_R" );

        sqp.setParameter("pmtObject", idObject );
        sqp.execute();

        return obtainResults_InJSON_TBObject( sqp.getResultList() );
    }

    public String createObject( TBObject tbObject )
    {
        var pmtFields = validatorFieldsObj( tbObject, 1 );

        if (!pmtFields.isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the Objects table! Broken rules: one of the fields (" + pmtFields +
                    ") contain greater than permitted. Please, check it again.";
        }

        pmtFields = validatorFieldsObj( tbObject, 2 );

        if (!pmtFields.isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the Objects table! Broken rules: one of the fields (" + pmtFields +
                    ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Objects_C" );

        sqp.setParameter( "pmtTitle", tbObject.getTitle() );
        sqp.setParameter( "pmtDescription", tbObject.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBObject( sqp.getResultList() );
    }

    public String updateObject( long idObject, TBObject tbObject )
    {

        if (tbObject.getId() == null)
        {
            return "** REASON: Unfortunately, the field 'ID' in the JSON format is not present in Object set. "+
                    "Put the field 'ID' and try it.";
        }

        if (tbObject.getId() != idObject)
        {
            return "** REASON: Unfortunately, the ID (" + idObject + ") whose parameter with described value "+
                    "is different than the ID filled (" + tbObject.getId() +
                    ") in set of Object by the JSON format. Fill the same correct ID in both.";
        }

        if (!validatorFieldsObj( tbObject, 1 ).isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to update the new ID " +
                   "in the Objects table! Broken rules: one of the fields (" + validatorFieldsObj( tbObject, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsObj( tbObject, 2 ).isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to update the new ID " +
                    "in the Objects table! Broken rules: one of the fields (" + validatorFieldsObj( tbObject, 2 ) +
                    ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Objects_U" );

        sqp.setParameter( "pmtObject", idObject );
        sqp.setParameter( "pmtTitle", tbObject.getTitle() );
        sqp.setParameter( "pmtDescription", tbObject.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBObject( sqp.getResultList() );
    }

    // Delete
    public String deleteObject( long idObj )
    {
        sqp = em.createNamedStoredProcedureQuery( "Objects_D" );

        sqp.setParameter( "pmtObject", idObj );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    private String obtainResults_InJSON_TBObject(List<Object[]> lstObjs )
    {
        jsonObj       = "";
        resultJSONObj = "";

        if (lstObjs.toString().contains( "ortunately" ))
        {
            // Eliminar colchetes para não gerar confusão com formato JSON.
            return lstObjs.toString().replace("[", "").replace("]", "" );
        }

        var mapperObj = new ObjectMapper().
                        enable( SerializationFeature.INDENT_OUTPUT ).
                        registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataObj = new LinkedHashMap<>();
            Field[] flistObj            = TBObject.class.getDeclaredFields();
            var x = 0;

            for (Object[] resultObj : lstObjs)
            {

                while (x <= TBObject.class.getDeclaredFields().length - 2)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataObj.put( flistObj[ x ].getName(), resultObj[ x ] );
                            break;
                        }

                        case 1,2:
                        {
                            dataObj.put( flistObj[ x ].getName(), resultObj[ x ].toString() );
                            break;
                        }

                        default:
                        {
                            dataObj.put( flistObj[ x ].getName(),
                                         resultObj[ x ] == null ? null : resultObj[ x ].toString()
                            );
                        }
                    }

                    x++;
                }

                jsonObj = mapperObj.writerWithDefaultPrettyPrinter().writeValueAsString( dataObj ) + ",";
                resultJSONObj += jsonObj;
                x = 0;
            }

            return resultJSONObj.substring( 0, resultJSONObj.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private String validatorFieldsObj( TBObject tbObject, int option )
    {
        String strTentativas = "";

        if (option == 1)
        {
            strTentativas +=
                charactLimitObj( "Title", tbObject.getTitle().trim(), 100, option ) +
                charactLimitObj( "Description", tbObject.getDescription().trim(), 250, option );
        }

        if (option == 2)
        {
            strTentativas +=
                charactLimitObj( "Title", tbObject.getTitle().trim(), 3, option ) +
                charactLimitObj( "Description", tbObject.getDescription().trim(), 3, option );
        }

        return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private String charactLimitObj( String strField, String strValueField, int lengthField, int option )
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
}