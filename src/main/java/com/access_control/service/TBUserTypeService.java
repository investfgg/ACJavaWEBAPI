package com.access_control.service;

import com.access_control.entity.TBUserType;
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
public class TBUserTypeService
{
    @PersistenceContext
    private final EntityManager em;
    StoredProcedureQuery sqp;

    String jsonUserType;
    String resultJSONUserType;

    public TBUserTypeService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllUserTypes()
    {
        sqp = em.createNamedStoredProcedureQuery( "UserTypes_R" );

        sqp.setParameter("pmtUserType", -1 ); // OBSERVAÇÃO: O valor '-1' traz todos os objetos.
        sqp.execute();

        return obtainResults_InJSON_TBUserType( sqp.getResultList() );
    }

    public String getUserType( long idUserType )
    {
        sqp = em.createNamedStoredProcedureQuery( "UserTypes_R" );

        sqp.setParameter("pmtUserType", idUserType );
        sqp.execute();

        return obtainResults_InJSON_TBUserType( sqp.getResultList() );
    }

    public String createUserType( TBUserType tbUserType )
    {
        var pmtFields = validatorFieldsUserType( tbUserType, 1 );

        if (!pmtFields.isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                   "in the UserTypes table! Broken rules: one of the fields (" + pmtFields +
                   ") contain greater than permitted. Please, check it again.";
        }

        pmtFields = validatorFieldsUserType( tbUserType, 2 );

        if (!pmtFields.isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to insert the new ID " +
                   "in the UserTypes table! Broken rules: one of the fields (" + pmtFields +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "UserTypes_C" );

        sqp.setParameter( "pmtTitle", tbUserType.getTitle() );
        sqp.setParameter( "pmtDescription", tbUserType.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBUserType( sqp.getResultList() );
    }

    public String updateUserType( long idUserType, TBUserType tbUserType )
    {

        if (tbUserType.getId() == null)
        {
            return "** REASON: Unfortunately, the field 'ID' in the JSON format is not present in UserTypes set. "+
                   "Put the field 'ID' and try it.";
        }

        if (tbUserType.getId() != idUserType)
        {
            return "** REASON: Unfortunately, the ID (" + idUserType + ") whose parameter with described value "+
                   "is different than the ID filled (" + tbUserType.getId() +
                   ") in set of UserTypes by the JSON format. Fill the same correct ID in both.";
        }

        if (!validatorFieldsUserType( tbUserType, 1 ).isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to update the new ID " +
                   "in the UserTypes table! Broken rules: one of the fields (" + validatorFieldsUserType( tbUserType, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsUserType( tbUserType, 2 ).isEmpty())
        {
            return "** REASON: Unfortunately, it was not possible to update the new ID " +
                   "in the UserTypes table! Broken rules: one of the fields (" + validatorFieldsUserType( tbUserType, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "UserTypes_U" );

        sqp.setParameter( "pmtUserType", idUserType );
        sqp.setParameter( "pmtTitle", tbUserType.getTitle() );
        sqp.setParameter( "pmtDescription", tbUserType.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBUserType( sqp.getResultList() );
    }

    public String deleteUserType( long idUserType )
    {
        sqp = em.createNamedStoredProcedureQuery( "UserTypes_D" );

        sqp.setParameter( "pmtUserType", idUserType );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    private String obtainResults_InJSON_TBUserType( List<Object[]> lstUserType )
    {
        jsonUserType       = "";
        resultJSONUserType = "";

        if (lstUserType.toString().contains( "ortunately" ))
        {
            // Eliminar colchetes para não gerar confusão com formato JSON.
            return lstUserType.toString().replace("[", "").replace("]", "" );
        }

        var mapperUsT = new ObjectMapper().
                        enable( SerializationFeature.INDENT_OUTPUT ).
                        registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataUserType = new LinkedHashMap<>();
            Field[] flistUserType            = TBUserType.class.getDeclaredFields();

            var x = 0;

            for (Object[] objUserType : lstUserType)
            {

                while (x <= TBUserType.class.getDeclaredFields().length - 1)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataUserType.put( flistUserType[ x ].getName(), objUserType[ x ] );
                            break;
                        }

                        case 1,2:
                        {
                            dataUserType.put( flistUserType[ x ].getName(), objUserType[ x ].toString() );
                            break;
                        }

                        default:
                        {
                            dataUserType.put( flistUserType[ x ].getName(),
                                    objUserType[ x ] == null ? null : objUserType[ x ].toString()
                            );
                        }
                    }

                    x++;
                }

                jsonUserType       = mapperUsT.writerWithDefaultPrettyPrinter().writeValueAsString( dataUserType ) + ",";
                resultJSONUserType += jsonUserType;
                x = 0;
            }

            return resultJSONUserType.substring( 0, resultJSONUserType.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private String validatorFieldsUserType( TBUserType tbUserType, int option )
    {
        String strTentativas = "";

        if (option == 1)
        {
            strTentativas +=
                charactLimitObj( "Title", tbUserType.getTitle().trim(), 100, option ) +
                charactLimitObj( "Description", tbUserType.getDescription().trim(), 250, option );
        }

        if (option == 2)
        {
            strTentativas +=
                charactLimitObj( "Title", tbUserType.getTitle().trim(), 3, option ) +
                charactLimitObj( "Description", tbUserType.getDescription().trim(), 3, option );
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