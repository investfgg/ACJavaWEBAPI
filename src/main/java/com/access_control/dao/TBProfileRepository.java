package com.access_control.dao;

import com.access_control.entity.TBProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TBProfileRepository extends CrudRepository<TBProfile, Long>
{
    // Não utilizarei os métodos do CrudRepository
    // pois estou utilizando Stored Procedures com regras internas
    // cujos métodos estão no 'TBProfileService'.
}