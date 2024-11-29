package com.access_control.dao;

import com.access_control.entity.TBPermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TBPermissionRepository extends CrudRepository<TBPermission, Long>
{
    // Não utilizarei os métodos do CrudRepository
    // pois estou utilizando Stored Procedures com regras internas
    // cujos métodos estão no 'TBPermissionService'.
}