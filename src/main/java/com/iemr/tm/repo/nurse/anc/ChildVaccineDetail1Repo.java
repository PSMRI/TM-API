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
package com.iemr.tm.repo.nurse.anc;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.tm.data.anc.ChildVaccineDetail1;

@Repository
@RestResource(exported = false)
public interface ChildVaccineDetail1Repo extends CrudRepository<ChildVaccineDetail1, Long>{
	
	@Query("select Date(createdDate), defaultReceivingAge, vaccineName, status "
			+ "from ChildVaccineDetail1 a where a.beneficiaryRegID = :beneficiaryRegID ORDER BY createdDate DESC ")
	public ArrayList<Object[]> getBenChildVaccineDetails(@Param("beneficiaryRegID") Long beneficiaryRegID);
	
	@Query("select beneficiaryRegID, benVisitID, providerServiceMapID, defaultReceivingAge, vaccineName, status, visitCode, sctCode, sctTerm"
			+ " from ChildVaccineDetail1 a where a.beneficiaryRegID = :beneficiaryRegID and a.visitCode = :visitCode")
	public ArrayList<Object[]> getBenChildVaccineDetails(@Param("beneficiaryRegID") Long beneficiaryRegID, 
			@Param("visitCode") Long visitCode);

	
	@Query(" SELECT defaultReceivingAge, vaccineName, processed from ChildVaccineDetail1 where beneficiaryRegID=:benRegID AND visitCode = :visitCode")
	public ArrayList<Object[]> getBenChildVaccineDetailStatus(@Param("benRegID") Long benRegID,
			@Param("visitCode") Long visitCode);
	
	@Transactional
	@Modifying
	@Query("update ChildVaccineDetail1 set status=:status, modifiedBy=:modifiedBy, processed=:processed,sctCode=:sctCode,sctTerm =:sctTerm"
			+ " where  beneficiaryRegID=:benRegID and visitCode = :visitCode  AND defaultReceivingAge=:defaultReceivingAge AND vaccineName=:vaccineName")
	public int updateChildANCImmunization(@Param("status") Boolean status,
			@Param("modifiedBy") String modifiedBy,
			@Param("processed") String processed,
			@Param("benRegID") Long benRegID,
			@Param("visitCode") Long visitCode,
			@Param("defaultReceivingAge") String defaultReceivingAge,
			@Param("vaccineName") String vaccineName,
			@Param("sctCode") String sctCode,
			@Param("sctTerm") String sctTerm);
	
}
