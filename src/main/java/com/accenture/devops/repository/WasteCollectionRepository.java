package com.accenture.devops.repository;

import com.accenture.devops.domain.WasteCollection;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the WasteCollection entity.
 */
@SuppressWarnings("unused")
public interface WasteCollectionRepository extends JpaRepository<WasteCollection,Long> {

}
