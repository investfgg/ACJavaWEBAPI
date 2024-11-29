package com.access_control.service;

import com.access_control.entity.TBPermission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TBPermissionService
{
    @PersistenceContext
    private final EntityManager em;
    StoredProcedureQuery sqp;

    String jsonPermission;
    String resultJSONPermission;

    public TBPermissionService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllPermissions()
    {
        sqp = em.createNamedStoredProcedureQuery( "Permissions_R" );

        sqp.setParameter("pmtPermission", -1 ); // OBSERVAÇÃO: O valor '-1' traz todos os objetos.
        sqp.execute();

        return obtainResults_InJSON_TBPermission( sqp.getResultList() );
    }

    public String getPermission( long idPermission )
    {
        sqp = em.createNamedStoredProcedureQuery( "Permissions_R" );

        sqp.setParameter("pmtPermission", idPermission );
        sqp.execute();

        return obtainResults_InJSON_TBPermission( sqp.getResultList() );
    }

    public String createPermission( TBPermission tbPermission )
    {
        var pmtFields = validatorFieldsPermission( tbPermission, 1 );

        if (!pmtFields.isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the Permissions table! Broken rules: one of the fields (" + pmtFields +
                    ") contain greater than permitted. Please, check it again.";
        }

        pmtFields = validatorFieldsPermission( tbPermission, 2 );

        if (!pmtFields.isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                    "in the Permissions table! Broken rules: one of the fields (" + pmtFields +
                    ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Permissions_C" );

        sqp.setParameter( "pmtTitle", tbPermission.getTitle() );
        sqp.setParameter( "pmtDescription", tbPermission.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBPermission( sqp.getResultList() );
    }

    public String updatePermission( long idPermission, TBPermission tbPermission )
    {

        if (tbPermission.getId() == null)
        {
            return "** REASON: Unfortunately, the field 'ID' in the JSON format is not present in Permissions set. "+
                   "Put the field 'ID' and try it.";
        }

        if (tbPermission.getId() != idPermission)
        {
            return "** REASON: Unfortunately, the ID (" + idPermission + ") whose parameter with described value "+
                   "is different than the ID filled (" + tbPermission.getId() +
                   ") in set of Permissions by the JSON format. Fill the same correct ID in both.";
        }

        if (!validatorFieldsPermission( tbPermission, 1 ).isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to update the new ID " +
                   "in the Permissions table! Broken rules: one of the fields (" +
                   validatorFieldsPermission( tbPermission, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsPermission( tbPermission, 2 ).isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to update the new ID " +
                   "in the Permissions table! Broken rules: one of the fields (" +
                   validatorFieldsPermission( tbPermission, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Permissions_U" );

        sqp.setParameter( "pmtPermission", idPermission );
        sqp.setParameter( "pmtTitle", tbPermission.getTitle() );
        sqp.setParameter( "pmtDescription", tbPermission.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBPermission( sqp.getResultList() );
    }

    public String deletePermission( long idPermission )
    {
        sqp = em.createNamedStoredProcedureQuery( "Permissions_D" );

        sqp.setParameter( "pmtPermission", idPermission );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    private String obtainResults_InJSON_TBPermission( List<Object[]> lstPermission )
    {
        jsonPermission       = "";
        resultJSONPermission = "";

        if (lstPermission.toString().contains( "ortunately" ))
        {
            // Eliminar colchetes para não gerar confusão com formato JSON.
            return lstPermission.toString().replace("[", "").replace("]", "" );
        }

        var mapperUsT = new ObjectMapper().
                        enable( SerializationFeature.INDENT_OUTPUT ).
                        registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataPermission = new LinkedHashMap<>();
            Field[] flistPermission            = TBPermission.class.getDeclaredFields();

            var x = 0;

            for (Object[] objPermission : lstPermission)
            {

                while (x <= TBPermission.class.getDeclaredFields().length - 1)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataPermission.put( flistPermission[ x ].getName(), objPermission[ x ] );
                            break;
                        }

                        case 1,2:
                        {
                            dataPermission.put( flistPermission[ x ].getName(), objPermission[ x ].toString() );
                            break;
                        }

                        default:
                        {
                            dataPermission.put( flistPermission[ x ].getName(),
                                          objPermission[ x ] == null ? null : objPermission[ x ].toString()
                            );
                        }
                    }

                    x++;
                }

                jsonPermission       = mapperUsT.writerWithDefaultPrettyPrinter().writeValueAsString( dataPermission ) + ",";
                resultJSONPermission += jsonPermission;
                x = 0;
            }

            return resultJSONPermission.substring( 0, resultJSONPermission.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private String validatorFieldsPermission( TBPermission tbPermission, int option )
    {
        String strTentativas = "";

        if (option == 1)
        {
            strTentativas +=
                charactLimitObj( "Title", tbPermission.getTitle().trim(), 100, option ) +
                charactLimitObj( "Description", tbPermission.getDescription().trim(), 250, option );
        }

        if (option == 2)
        {
            strTentativas +=
                charactLimitObj( "Title", tbPermission.getTitle().trim(), 3, option ) +
                charactLimitObj( "Description", tbPermission.getDescription().trim(), 3, option );
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