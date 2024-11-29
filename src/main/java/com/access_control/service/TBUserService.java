package com.access_control.service;

import com.access_control.entity.TBUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TBUserService
{
    @PersistenceContext
    private final EntityManager em;
    StoredProcedureQuery sqp;

    int qtdeUsers;
    String[] fieldsUser = { "id", "name", "email", "description", "created_at", "updated_at", "deleted_at" };
    String[] flistUsrAcc = { "id", "username", "password", "tip", "created_at", "updated_at", "deleted_at" };
    String jsonUser;
    String resultJSONUser = "";

    String strReasonCreate = "** REASON: Unfortunately, it was not possible to insert the new ID " +
            "in the Users table! Broken rules: one of the fields (";

    String strReasonUpdate = "** REASON: Unfortunately, ";

    public TBUserService( EntityManager em )
    {
        this.em = em;
    }

    public String getAllUsers()
    {
        sqp = em.createNamedStoredProcedureQuery( "Users_R" );

        sqp.setParameter("pmtUser", -1 );

        sqp.execute();

        return obtainResults_InJSON_TBUser( sqp.getResultList() );
    }

    public String getUser( long idUser )
    {
        sqp = em.createNamedStoredProcedureQuery( "Users_R" );

        sqp.setParameter("pmtUser", idUser );

        sqp.execute();

        return obtainResults_InJSON_TBUser( sqp.getResultList() );
    }

    public String createUser( TBUser tbUser )
    {

        if (!validatorFieldsUser( tbUser, 1 ).isEmpty())
        {
            return strReasonCreate + validatorFieldsUser( tbUser, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsUser( tbUser, 2 ).isEmpty())
        {
            return strReasonCreate + validatorFieldsUser( tbUser, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Users_C" );

        sqp.setParameter("pmtName", tbUser.getName() );
        sqp.setParameter("pmtEmail", tbUser.getEmail() );
        sqp.setParameter("pmtDescription", tbUser.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBUser( sqp.getResultList() );
    }

    public String updateUser( long idUser, TBUser tbUser )
    {

        if (tbUser.getId() == null)
        {
            return strReasonUpdate + "the field 'ID' in the JSON format is not present in User set. " +
                   "Put the field and try it.";
        }

        if (tbUser.getId() != idUser)
        {
            return strReasonUpdate + "the ID (" + idUser + ") whose parameter with described value " +
                   "is different than the ID filled (" + tbUser.getId() +
                   ") in set of User by the JSON format. Fill the same correct ID in both.";
        }

        if (!validatorFieldsUser( tbUser, 1 ).isEmpty())
        {
            return strReasonUpdate + "it was not possible to update the actual ID (" + idUser +
                   ") in the Users table! Broken rules: one of the fields (" + validatorFieldsUser( tbUser, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsUser( tbUser, 2 ).isEmpty())
        {
            return strReasonUpdate + "it was not possible to update the new ID " +
                   "in the Users table! Broken rules: one of the fields (" + validatorFieldsUser( tbUser, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "Users_U" );

        sqp.setParameter( "pmtUser", idUser );
        sqp.setParameter( "pmtName", tbUser.getName() );
        sqp.setParameter( "pmtEmail", tbUser.getEmail() );
        sqp.setParameter( "pmtDescription", tbUser.getDescription() );

        sqp.execute();

        return obtainResults_InJSON_TBUser( sqp.getResultList() );
    }

    public String deleteUser( long idUser )
    {
        sqp = em.createNamedStoredProcedureQuery( "Users_D" );

        sqp.setParameter("pmtUser", idUser );
        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    public String report_UsrAccByUser()
    {
        sqp = em.createNamedStoredProcedureQuery( "lstUsr" );

        sqp.setParameter("pmtUser", -1 );
        sqp.execute();

        if (sqp.getResultList().toString().contains( "ortunately" ))
        {
            return sqp.getResultList().toString().replace("[", "").replace("]", "" );
        }

        return obtainReport_InJSON_TBUser( sqp.getResultList() );
    }

    public String report_IdUsrAccByUser( long idUser )
    {
        sqp = em.createNamedStoredProcedureQuery( "relUsrAccByUser" );

        sqp.setParameter("pmtUser", idUser );
        sqp.execute();

        if (sqp.getResultList().toString().contains( "ortunately" ))
        {
            return sqp.getResultList().toString().replace("[", "").replace("]", "" );
        }

        return obtainReport_InJSON_TBUser( sqp.getResultList() );
    }

    private String obtainResults_InJSON_TBUser( List<Object[]> listTBUser )
    {
        jsonUser = "";
        resultJSONUser = "";

        if (listTBUser.toString().contains( "ortunately" ))
        {
            // Eliminar colchetes para não gerar confusão com formato JSON.
            // Resultado recém-sa´dio diretamente no Stored Procedure.
            return listTBUser.toString().replace("[", "").replace("]", "");
        }

        var mapperUser = new ObjectMapper().
                         enable( SerializationFeature.INDENT_OUTPUT ).
                         registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataMapUser = new LinkedHashMap<>();

            var x = 0;

            for (Object[] arrObjUser : listTBUser)
            {

                while (x <= fieldsUser.length - 1)
                {

                    if (x == 0)
                    {
                        dataMapUser.put( fieldsUser[ x ], arrObjUser[ x ] );
                    }

                    else if (x <= 4)
                    {
                        dataMapUser.put( fieldsUser[ x ], arrObjUser[ x ].toString() );
                    }

                    else
                    {
                        dataMapUser.put( fieldsUser[ x ], arrObjUser[ x ] == null ? null : arrObjUser[ x ].toString() );
                    }

                    x++;
                }

                jsonUser = mapperUser.writerWithDefaultPrettyPrinter().writeValueAsString( dataMapUser ) + ",";
                resultJSONUser += jsonUser;
                x = 0;
            }

            return resultJSONUser.substring( 0, resultJSONUser.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private String validatorFieldsUser( TBUser tbUser, int option )
    {
        String strTentativas = "";

        if (option == 1)
        {
            strTentativas +=
                charactLimitUser( "Name", tbUser.getName().trim(), 100, option ) +
                charactLimitUser( "Email", tbUser.getEmail().trim(), 100, option ) +
                charactLimitUser( "Description", tbUser.getDescription().trim(), 250, option );
        }

        if (option == 2)
        {
            strTentativas +=
                charactLimitUser( "Name", tbUser.getName().trim(), 3, option ) +
                charactLimitUser( "Title", tbUser.getEmail().trim(), 3, option ) +
                charactLimitUser( "Description", tbUser.getDescription().trim(),3, option );
        }

        return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private String charactLimitUser( String strField, String strValueField, int lengthField, int option )
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

    private String obtainReport_InJSON_TBUser( List<Object[]> lstTBUser )
    {
        jsonUser = "";
        resultJSONUser = "";

        try
        {

            for (Object[] result : lstTBUser)
            {
                jsonUser = mountMapForJSONUser( fieldsUser, result );
                jsonUser = jsonUser.replace("\n" + "}{ }", "}");
                jsonUser = jsonUser.replace("\n" + "}{", ",");

                resultJSONUser += jsonUser + ",";
            }

            return resultJSONUser.substring( 0, resultJSONUser.length() - 1 );
        }

        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
    }

    private String mountMapForJSONUser( String[] flist, Object[] result )
    {
        String resultMountJSON;
        String mapperString;
        long idUser = 0;

        var mapper = new ObjectMapper().
                     enable( SerializationFeature.INDENT_OUTPUT ).
                     registerModule( new JavaTimeModule() );

        int x = 0;
        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, Object> dataUser = new LinkedHashMap<>();

        while (x <= 7)
        {

            if (x == 0)
            {
                idUser = (long) result[ x ];
                data.put( flist[ x ], result[ x ] );
            }

            else if (x <= 6)
            {
                var value = result[ x ] != null ? result[ x ].toString() : null;
                data.put( flist[ x ], value );
            }

            else
            {
                sqp = em.createNamedStoredProcedureQuery( "fullUsrAccess_us" );
                sqp.setParameter( "pmtUser", idUser );
                sqp.execute();

                qtdeUsers = sqp.getResultList().size();

                List<Object[]> rsId = sqp.getResultList();
                var posObj = 1;

                for (Object[] resultID : rsId)
                {
                    dataUser.put( "usr_access_" + posObj, fullUsrAccessIDJSON2( resultID ) );
                    posObj++;
                }

                if (dataUser.isEmpty())
                {
                    data.put( "usr_access", "no information" );
                }
            }

            x++;
        }

        try
        {
            mapperString = mapper.writerWithDefaultPrettyPrinter().
                           writeValueAsString( data ).replace("ion\",", "ion\"" ) +
                           mapper.writerWithDefaultPrettyPrinter().
                           writeValueAsString( dataUser );    // + ",";

            mapperString = mapperString.replace("\n" + "}{ }", "}" );
            mapperString = mapperString.replace("\n" + "}{", "," );

            resultMountJSON = mapperString + ",";
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }

        return resultMountJSON.substring( 0, resultMountJSON.length() - 1 );
    }

    private Map<String, Object> fullUsrAccessIDJSON2( Object[] resultID )
    {
        Map<String, Object> result = new LinkedHashMap<>();

        int qtdeFields = flistUsrAcc.length;
        var y = 0;

        Map<String, Object> dataUs = new LinkedHashMap<>();

        while (y <= qtdeFields - 1)
        {

            if (y <= 3)
            {
                dataUs.put( flistUsrAcc[ y ], resultID[ y ] );
            }

            else
            {
                dataUs.put( flistUsrAcc[ y ], (resultID[ y ] == null) ? null : resultID[ y ].toString() );
            }

            y++;
        }

        result.putAll( dataUs );

        for ( var xy = 1; xy <= result.size() / 7; xy++ )
        {

            for (var xz = 0; xz <= 6; xz++ )
            {
                //data.values().toArray()[7].toString().replace("_2", "")
                String replaced = ((String) result.keySet().toArray()[ xz ]).replace("_" + xy, "");
                result.keySet().toArray()[ xz ] = replaced;
            }
        }

        return result;
    }
}