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
package com.iemr.tm.service.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iemr.tm.data.location.Country;
import com.iemr.tm.data.location.CountryCityMaster;
import com.iemr.tm.data.location.DistrictBlock;
import com.iemr.tm.data.location.DistrictBranchMapping;
import com.iemr.tm.data.location.Districts;
import com.iemr.tm.data.location.States;
import com.iemr.tm.data.location.V_GetLocDetailsFromSPidAndPSMid;
import com.iemr.tm.data.location.ZoneMaster;
import com.iemr.tm.data.login.MasterServicePoint;
import com.iemr.tm.data.login.ParkingPlace;
import com.iemr.tm.data.login.ServicePointVillageMapping;
import com.iemr.tm.repo.location.CountryCityMasterRepo;
import com.iemr.tm.repo.location.CountryMasterRepo;
import com.iemr.tm.repo.location.DistrictBlockMasterRepo;
import com.iemr.tm.repo.location.DistrictBranchMasterRepo;
import com.iemr.tm.repo.location.DistrictMasterRepo;
import com.iemr.tm.repo.location.ParkingPlaceMasterRepo;
import com.iemr.tm.repo.location.ServicePointMasterRepo;
import com.iemr.tm.repo.location.StateMasterRepo;
import com.iemr.tm.repo.location.V_GetLocDetailsFromSPidAndPSMidRepo;
import com.iemr.tm.repo.location.V_getVanLocDetailsRepo;
import com.iemr.tm.repo.location.V_get_prkngplc_dist_zone_state_from_spidRepo;
import com.iemr.tm.repo.location.ZoneMasterRepo;
import com.iemr.tm.repo.login.ServicePointVillageMappingRepo;

@Service
public class LocationServiceImpl implements LocationService {

	@Autowired
	private CountryMasterRepo countryMasterRepo;
	@Autowired
	private CountryCityMasterRepo countryCityMasterRepo;

	private StateMasterRepo stateMasterRepo;
	private ZoneMasterRepo zoneMasterRepo;
	private DistrictMasterRepo districtMasterRepo;
	private DistrictBlockMasterRepo districtBlockMasterRepo;
	private ParkingPlaceMasterRepo parkingPlaceMasterRepo;
	private ServicePointMasterRepo servicePointMasterRepo;
	private V_GetLocDetailsFromSPidAndPSMidRepo v_GetLocDetailsFromSPidAndPSMidRepo;
	private ServicePointVillageMappingRepo servicePointVillageMappingRepo;
	private DistrictBranchMasterRepo districtBranchMasterRepo;
	private V_get_prkngplc_dist_zone_state_from_spidRepo v_get_prkngplc_dist_zone_state_from_spidRepo;
	@Autowired
	private V_getVanLocDetailsRepo v_getVanLocDetailsRepo;

	@Autowired
	public void setV_get_prkngplc_dist_zone_state_from_spidRepo(
			V_get_prkngplc_dist_zone_state_from_spidRepo v_get_prkngplc_dist_zone_state_from_spidRepo) {
		this.v_get_prkngplc_dist_zone_state_from_spidRepo = v_get_prkngplc_dist_zone_state_from_spidRepo;
	}

	@Autowired
	public void setDistrictBranchMasterRepo(DistrictBranchMasterRepo districtBranchMasterRepo) {
		this.districtBranchMasterRepo = districtBranchMasterRepo;
	}

	@Autowired
	public void setServicePointVillageMappingRepo(ServicePointVillageMappingRepo servicePointVillageMappingRepo) {
		this.servicePointVillageMappingRepo = servicePointVillageMappingRepo;
	}

	@Autowired
	public void setV_GetLocDetailsFromSPidAndPSMidRepo(
			V_GetLocDetailsFromSPidAndPSMidRepo v_GetLocDetailsFromSPidAndPSMidRepo) {
		this.v_GetLocDetailsFromSPidAndPSMidRepo = v_GetLocDetailsFromSPidAndPSMidRepo;
	}

	@Autowired
	public void setServicePointMasterRepo(ServicePointMasterRepo servicePointMasterRepo) {
		this.servicePointMasterRepo = servicePointMasterRepo;
	}

	@Autowired
	public void setParkingPlaceMasterRepo(ParkingPlaceMasterRepo parkingPlaceMasterRepo) {
		this.parkingPlaceMasterRepo = parkingPlaceMasterRepo;
	}

	@Autowired
	public void setDistrictBlockMasterRepo(DistrictBlockMasterRepo districtBlockMasterRepo) {
		this.districtBlockMasterRepo = districtBlockMasterRepo;
	}

	@Autowired
	public void setDistrictMasterRepo(DistrictMasterRepo districtMasterRepo) {
		this.districtMasterRepo = districtMasterRepo;
	}

	@Autowired
	public void setZoneMasterRepo(ZoneMasterRepo zoneMasterRepo) {
		this.zoneMasterRepo = zoneMasterRepo;
	}

	@Autowired
	public void setStateMasterRepo(StateMasterRepo stateMasterRepo) {
		this.stateMasterRepo = stateMasterRepo;
	}

	@Override
	public String getCountryList() {

		ArrayList<Country> stateMasterList = countryMasterRepo.findAllCountries();

		return new Gson().toJson(stateMasterList);
	}

	@Override
	public String getCountryCityList(Integer countryID) {

		ArrayList<CountryCityMaster> countryCityList = countryCityMasterRepo.findByCountryIDAndDeleted(countryID,
				false);

		return new Gson().toJson(countryCityList);
	}

	@Override
	public String getStateList() {
		ArrayList<States> stateList = new ArrayList<>();
		ArrayList<Object[]> stateMasterList = stateMasterRepo.getStateMaster();
		if (stateMasterList != null && stateMasterList.size() > 0) {
			for (Object[] objArr : stateMasterList) {
				States states = new States((Integer) objArr[0], (String) objArr[1]);
				stateList.add(states);
			}
		}
		return new Gson().toJson(stateList);
	}

	@Override
	public String getZoneList(Integer providerServiceMapID) {
		ArrayList<Object> zoneList = new ArrayList<>();
		ArrayList<Object[]> zoneMasterList = zoneMasterRepo.getZoneMaster(providerServiceMapID);
		if (zoneMasterList != null && zoneMasterList.size() > 0) {
			for (Object[] objArr : zoneMasterList) {
				ZoneMaster zoneMaster = new ZoneMaster((Integer) objArr[0], (String) objArr[1]);
				zoneList.add(zoneMaster);
			}
		}
		return new Gson().toJson(zoneList);
	}

	@Override
	public String getDistrictList(Integer stateID) {
		ArrayList<Object> districtList = new ArrayList<>();
		ArrayList<Object[]> districtMasterList = districtMasterRepo.getDistrictMaster(stateID);
		if (districtMasterList != null && districtMasterList.size() > 0) {
			for (Object[] objArr : districtMasterList) {
				Districts districtMaster = new Districts((Integer) objArr[0], (String) objArr[1], (Integer) objArr[2],
						(Integer) objArr[3]);
				districtList.add(districtMaster);
			}
		}
		return new Gson().toJson(districtList);
	}

	@Override
	public String getDistrictBlockList(Integer districtID) {
		ArrayList<Object> districtBlockList = new ArrayList<>();
		ArrayList<Object[]> districtBlockMasterList = districtBlockMasterRepo.getDistrictBlockMaster(districtID);
		if (districtBlockMasterList != null && districtBlockMasterList.size() > 0) {
			for (Object[] objArr : districtBlockMasterList) {
				DistrictBlock districtBLockMaster = new DistrictBlock((Integer) objArr[0], (String) objArr[1]);
				districtBlockList.add(districtBLockMaster);
			}
		}
		return new Gson().toJson(districtBlockList);
	}

	@Override
	public String getParkingPlaceList(Integer providerServiceMapID) {
		ArrayList<Object> parkingPlaceList = new ArrayList<>();
		ArrayList<Object[]> parkingPlaceMasterList = parkingPlaceMasterRepo.getParkingPlaceMaster(providerServiceMapID);
		if (parkingPlaceMasterList != null && parkingPlaceMasterList.size() > 0) {
			for (Object[] objArr : parkingPlaceMasterList) {
				ParkingPlace parkingPlace = new ParkingPlace((Integer) objArr[0], (String) objArr[1]);
				parkingPlaceList.add(parkingPlace);
			}
		}
		return new Gson().toJson(parkingPlaceList);
	}

	public String getServicePointPlaceList(Integer parkingPlaceID) {
		ArrayList<Object> servicePointList = new ArrayList<>();
		ArrayList<Object[]> servicePointMasterList = servicePointMasterRepo.getServicePointMaster(parkingPlaceID);
		if (servicePointMasterList != null && servicePointMasterList.size() > 0) {
			for (Object[] objArr : servicePointMasterList) {
				MasterServicePoint masterServicePoint = new MasterServicePoint((Integer) objArr[0], (String) objArr[1]);
				servicePointList.add(masterServicePoint);
			}
		}
		return new Gson().toJson(servicePointList);
	}

	public String getVillageMasterFromBlockID(Integer distBlockID) {
		ArrayList<Object[]> resList = districtBranchMasterRepo.findByBlockID(distBlockID);
		return DistrictBranchMapping.getVillageList(resList);
	}

	// new, 11-10-2018
	public String getLocDetailsNew(Integer vanID, Integer spPSMID) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		ArrayList<Object[]> resultSet = v_getVanLocDetailsRepo.getVanLocDetails(vanID);

		// state master
		ArrayList<States> stateList = new ArrayList<>();
		ArrayList<Object[]> stateMasterList = stateMasterRepo.getStateMaster();
		if (stateMasterList != null && stateMasterList.size() > 0) {
			for (Object[] objArr : stateMasterList) {
				States states = new States((Integer) objArr[0], (String) objArr[1], (Integer) objArr[2]);
				stateList.add(states);
			}
		}

		resMap.put("otherLoc", getDefaultLocDetails(resultSet));
		resMap.put("stateMaster", stateList);

		return new Gson().toJson(resMap);
	}

	private Map<String, Object> getDefaultLocDetails(ArrayList<Object[]> objList) {
		Map<String, Object> returnObj = new HashMap<>();
		Map<String, Object> distMap = new HashMap<>();
		ArrayList<Map<String, Object>> distLit = new ArrayList<>();
		if (objList != null && objList.size() > 0) {
			returnObj.put("stateID", objList.get(0)[0]);
			returnObj.put("parkingPlaceID", objList.get(0)[1]);
			for (Object[] objArr : objList) {
				distMap = new HashMap<>();
				distMap.put("districtID", objArr[2]);
				distMap.put("districtName", objArr[3]);

				distLit.add(distMap);
			}

			returnObj.put("districtList", distLit);
		}
		return returnObj;

	}
}
