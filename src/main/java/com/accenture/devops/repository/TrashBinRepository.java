package com.accenture.devops.repository;

import com.accenture.devops.domain.TrashBin;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TrashBin entity.
 */
@SuppressWarnings("unused")
public interface TrashBinRepository extends JpaRepository<TrashBin,Long> {

}
