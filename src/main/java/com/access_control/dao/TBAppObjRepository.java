package com.access_control.dao;

import com.access_control.entity.TBAppObj;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TBAppObjRepository extends CrudRepository<TBAppObj, Long>
{
    // Não utilizarei os métodos do CrudRepository
    // pois estou utilizando Stored Procedures com regras internas
    // cujos métodos estão no 'TBAppObjService'.
}