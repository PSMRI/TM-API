/*
* AMRIT – Accessible Medical Records via Integrated Technology
* Integrated EHR (Electronic Health Records) Solution
*
* Copyright (C) "Piramal Swasthya Management and Research Institute"
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.tm.repo.login;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.iemr.tm.data.login.ServicePointVillageMapping;

@Repository

public interface ServicePointVillageMappingRepo extends CrudRepository<ServicePointVillageMapping, Integer>{
	@Query("SELECT d.districtBranchID,d.villageName from ServicePointVillageMapping s "
			+ " INNER JOIN s.districtBranchMapping d"
			+ " WHERE s.servicePointID = :servicePointID and s.deleted != true ")
	public List<Object[]> getServicePointVillages(@Param("servicePointID") Integer servicePointID);
}
