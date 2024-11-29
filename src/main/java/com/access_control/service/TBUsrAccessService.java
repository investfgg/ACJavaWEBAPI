package com.access_control.service;

import com.access_control.aes.AESUtil;
import com.access_control.entity.TBUsrAccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import jakarta.transaction.Transactional;

import java.security.NoSuchAlgorithmException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import org.hibernate.procedure.internal.ProcedureCallImpl;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

@Service
public class TBUsrAccessService
{
    @PersistenceContext
    private final EntityManager em;

    StoredProcedureQuery sqp;
    StoredProcedureQuery sqp2;

    String jsonUsrAccess;
    String resultJSONUsrAccess;

    String strReasonCreate = "** REASON: Unfortunately, it was not possible to insert the new ID " +
            "in the UsrAccess table! Broken rules: one of the fields (";

    String strReasonUpdate = "** REASON: Unfortunately, ";

    // Variáveis para processamento do campo 'Password' - Favor não removê-las
    SecretKey key = AESUtil.generateKey(128 );
    IvParameterSpec ivParameterSpec = AESUtil.generateIv();
    String algorithm = "AES/CBC/PKCS5Padding";

    public TBUsrAccessService( EntityManager em ) throws NoSuchAlgorithmException
    {
        this.em = em;
    }

    public String getAllUsrAccesses()
    {
        sqp = em.createNamedStoredProcedureQuery( "UsrAccess_R" );

        sqp.setParameter("pmtUsrAccess", -1 );

        sqp.execute();

        return obtainResults_InJSON_TBUsrAccess( sqp.getResultList() );
    }

    public String getUsrAccess( long idUsrAccess )
    {
        sqp = em.createNamedStoredProcedureQuery( "UsrAccess_R" );

        sqp.setParameter("pmtUsrAccess", idUsrAccess );

        sqp.execute();

        return obtainResults_InJSON_TBUsrAccess( sqp.getResultList() );
    }

    @SneakyThrows
    public String createUsrAccess( TBUsrAccess tbUsrAccess )
    {
        long idUsers = tbUsrAccess.getUsers().getId();

        if (!validatorFieldsUsrAccess( tbUsrAccess, idUsers, 1 ).isEmpty())
        {
            return strReasonCreate + validatorFieldsUsrAccess( tbUsrAccess, idUsers, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsUsrAccess( tbUsrAccess, idUsers, 2 ).isEmpty())
        {
            return strReasonCreate + validatorFieldsUsrAccess( tbUsrAccess, idUsers,2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "UsrAccess_C" );

        sqp.setParameter("pmtUserName", tbUsrAccess.getUserName() );
        sqp.setParameter("pmtPassword",
                          AESUtil.encrypt( algorithm, tbUsrAccess.getPassword(), key, ivParameterSpec ) );
        sqp.setParameter("pmtTip", tbUsrAccess.getTip() );
        sqp.setParameter("pmtUser", idUsers );

        sqp.execute();

        return obtainResults_InJSON_TBUsrAccess( sqp.getResultList() );
    }

    @SneakyThrows
    public String updateUsrAccess( long idUsrAccess, TBUsrAccess tbUsrAccess )
    {

        if (tbUsrAccess.getId() == null)
        {
            return strReasonUpdate + "the field 'ID' in the JSON format is not present in UsrAccess set. "+
                   "Put the field and try it.";
        }

        if (tbUsrAccess.getId() != idUsrAccess)
        {
            return strReasonUpdate + "the ID (" + idUsrAccess + ") whose parameter with described value " +
                   "is different than the ID filled (" + tbUsrAccess.getId() +
                    ") in set of UsrAccess by the JSON format. Fill the same correct ID in both.";
        }

        if (!validatorFieldsUsrAccess( tbUsrAccess, -1, 1 ).isEmpty())
        {
            return strReasonUpdate + "it was not possible to update the actual ID (" + idUsrAccess +
                   ") in the UsrAccess table! Broken rules: one of the fields (" +
                   validatorFieldsUsrAccess( tbUsrAccess, -1, 1 ) +
                   ") contain greater than permitted. Please, check it again.";
        }

        if (!validatorFieldsUsrAccess( tbUsrAccess, -1, 2 ).isEmpty())
        {
            return strReasonUpdate + "it was not possible to update the new ID " +
                   "in the UsrAccess table! Broken rules: one of the fields (" +
                   validatorFieldsUsrAccess( tbUsrAccess, -1, 2 ) +
                   ") is empty or contain less than 3 characters. Please, check it again.";
        }

        sqp = em.createNamedStoredProcedureQuery( "UsrAccess_U" );

        sqp.setParameter( "pmtUsrAccess", idUsrAccess );
        sqp.setParameter( "pmtUserName", tbUsrAccess.getUserName() );
        sqp.setParameter( "pmtPassword",
                          AESUtil.encrypt( algorithm, tbUsrAccess.getPassword(), key, ivParameterSpec ) );
        sqp.setParameter( "pmtTip", tbUsrAccess.getTip() );

        sqp.execute();

        return obtainResults_InJSON_TBUsrAccess( sqp.getResultList() );
    }

    public String deleteUsrAccess( long idUsrAccess )
    {
        sqp = em.createNamedStoredProcedureQuery( "UsrAccess_D" );

        sqp.setParameter("pmtUsrAccess", idUsrAccess );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    public String reportUsrAccess()
    {
        sqp = em.createNamedStoredProcedureQuery( "relUsrAccess" );

        sqp.execute();

        return sqp.getResultList().toString().replace("[", "").replace("]", "" );
    }

    private String obtainResults_InJSON_TBUsrAccess(List<Object[]> lstTBUsrAccess )
    {
        jsonUsrAccess       = "";
        resultJSONUsrAccess = "";

        if (lstTBUsrAccess.toString().contains( "ortunately" ))
        {
            return lstTBUsrAccess.toString().replace("[", "").replace("]", "");
        }

        var mapperUsrAccess = new ObjectMapper().
                              enable( SerializationFeature.INDENT_OUTPUT ).
                              registerModule( new JavaTimeModule() );

        try
        {
            Map<String, Object> dataUsrAccess = new LinkedHashMap<>();

            //Field[] flistUsrAccess = TBUsrAccess.class.getDeclaredFields();
            String[] flistUsrAccess = { "id", "username", "password", "tip", "id_users",
                                        "created_at", "updated_at", "deleted_at" };
            var x = 0;

            for (Object[] resultUsrAccess : lstTBUsrAccess)
            {

                while (x <= flistUsrAccess.length - 1) // TBUsrAccess.class.getDeclaredFields().length - 1)
                {

                    switch (x)
                    {
                        case 0:
                        {
                            dataUsrAccess.put( flistUsrAccess[ x ], resultUsrAccess[ x ] );
                            break;
                        }

                        case 1,2,3:
                        {
                            dataUsrAccess.put( flistUsrAccess[ x ], resultUsrAccess[ x ].toString() );
                            break;
                        }

                        case 4:
                        {
                            dataUsrAccess.put( flistUsrAccess[ x ], fullUserIDJSON( (long) resultUsrAccess[ x ] ) );
                            break;
                        }

                        default:
                        {
                            dataUsrAccess.put( flistUsrAccess[ x ],
                                               resultUsrAccess[ x ] == null ? null : resultUsrAccess[ x ].toString() );
                            break;
                        }
                    }

                    x++;
                }

                jsonUsrAccess = mapperUsrAccess.
                                writerWithDefaultPrettyPrinter().
                                writeValueAsString( dataUsrAccess ) + ",";

                resultJSONUsrAccess += jsonUsrAccess;
                x = 0;
            }

            return resultJSONUsrAccess.substring( 0, resultJSONUsrAccess.length() - 1 );
        }

        catch (JsonProcessingException e)
        {
            throw new RuntimeException( e );
        }
    }

    private String validatorFieldsUsrAccess( TBUsrAccess tbUsrAccess, long idUser, int option )
    {
        String strTentativas = "";

        if (option == 1)
        {
            strTentativas +=
                    charactLimitUsrAccess( "Username", tbUsrAccess.getUserName().trim(), 100, option ) +
                    charactLimitUsrAccess( "Password", tbUsrAccess.getPassword().trim(), 100, option ) +
                    charactLimitUsrAccess( "Tip", tbUsrAccess.getTip().trim(), 100, option );
        }

        if (option == 2)
        {
            strTentativas +=
                    charactLimitUsrAccess( "Username", tbUsrAccess.getUserName().trim(), 3, option ) +
                    charactLimitUsrAccess( "Password", tbUsrAccess.getPassword().trim(), 3, option ) +
                    charactLimitUsrAccess( "Tip", tbUsrAccess.getTip().trim(), 3, option );
        }

        if (idUser != -1)
        {

            if (idUser == 0)
            {
                strTentativas += "Id_Users (no code), ";
            }

            else
            {
                strTentativas += idExistinTBUser( idUser ) == 0 ? "Id_Users (not exists), " : "";
            }
        }

        return (!strTentativas.isEmpty()) ? strTentativas.substring( 0, strTentativas.length() - 2 ) : "";
    }

    private String charactLimitUsrAccess( String strField, String strValueField, int lengthField, int option )
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

    @Transactional
    private long idExistinTBUser( long idUser )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsrAccess_User" );

        sqp2.setParameter( "pmtUser", idUser );
        sqp2.setParameter( "pmtOperation", "ID" );

        sqp2.execute();

        /* "1" : Encontrado ** "0" : Não encontrado - string resultPmt = outputPmt == "0" ? "Id_Users, " : ""; */

        return (long) ((ProcedureCallImpl<?>) sqp2).getSingleResultOrNull();
    }

    private Map<String, Object> fullUserIDJSON( long idUser )
    {
        sqp2 = em.createNamedStoredProcedureQuery( "UsrAccess_User" );

        sqp2.setParameter( "pmtUser", idUser );
        sqp2.setParameter( "pmtOperation", "Full" );

        sqp2.execute();

        List<Object[]> listUser = sqp2.getResultList();
        Map<String, Object> dataUser = new LinkedHashMap<>();

        String[] flistUser = { "id", "name", "email", "description", "created_at", "updated_at", "deleted_at" };
        int qtdeFields     = flistUser.length - 1;
        var y = 0;

        for (Object[] resultID : listUser)
        {

            while (y <= qtdeFields)
            {

                if (y <= 3)
                {
                    dataUser.put( flistUser[ y ], resultID[ y ] );
                }

                else
                {

                    if (resultID[ y ] == null)
                    {
                        dataUser.put( flistUser[ y ], null );
                    }

                    else
                    {
                        dataUser.put( flistUser[ y ], resultID[ y ].toString() );
                    }
                }

                y++;
            }
        }

        return dataUser;
    }
}