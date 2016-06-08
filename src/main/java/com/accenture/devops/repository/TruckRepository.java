package com.accenture.devops.repository;

import com.accenture.devops.domain.Truck;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Truck entity.
 */
@SuppressWarnings("unused")
public interface TruckRepository extends JpaRepository<Truck,Long> {

}
