package com.iemr.tm.data.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class DistrictBranchMappingDiffblueTest {
    /**
     * Method under test: {@link DistrictBranchMapping#getVillageList(ArrayList)}
     */
    @Test
    void testGetVillageList() {
        // Arrange, Act and Assert
        assertEquals("[]", DistrictBranchMapping.getVillageList(new ArrayList<>()));
    }

    /**
     * Method under test: {@link DistrictBranchMapping#setDistrictBranchID(int)}
     */
    @Test
    void testSetDistrictBranchID() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();

        // Act
        districtBranchMapping.setDistrictBranchID(1);

        // Assert
        assertEquals(1, districtBranchMapping.getDistrictBranchID().intValue());
    }

    /**
     * Method under test: {@link DistrictBranchMapping#setDistrictBranchID(int)}
     */
    @Test
    void testSetDistrictBranchID2() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();
        districtBranchMapping.setBlockID(1);
        districtBranchMapping.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        districtBranchMapping.setCreatedDate(mock(Timestamp.class));
        districtBranchMapping.setDeleted(true);
        districtBranchMapping.setDistrictBranchID(1);
        districtBranchMapping.setHabitat("Habitat");
        districtBranchMapping.setLastModDate(mock(Timestamp.class));
        districtBranchMapping.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        districtBranchMapping.setPanchayatName("Panchayat Name");
        districtBranchMapping.setPinCode("Pin Code");
        districtBranchMapping.setVillageName("Village Name");

        // Act
        districtBranchMapping.setDistrictBranchID(1);

        // Assert
        assertEquals(1, districtBranchMapping.getDistrictBranchID().intValue());
    }

    /**
     * Method under test: {@link DistrictBranchMapping#setBlockID(int)}
     */
    @Test
    void testSetBlockID() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();

        // Act
        districtBranchMapping.setBlockID(1);

        // Assert
        assertEquals(1, districtBranchMapping.getBlockID().intValue());
    }

    /**
     * Method under test: {@link DistrictBranchMapping#setBlockID(int)}
     */
    @Test
    void testSetBlockID2() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();
        districtBranchMapping.setCreatedDate(mock(Timestamp.class));

        // Act
        districtBranchMapping.setBlockID(1);

        // Assert
        assertEquals(1, districtBranchMapping.getBlockID().intValue());
    }

    /**
     * Method under test: {@link DistrictBranchMapping#setDeleted(boolean)}
     */
    @Test
    void testSetDeleted() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();

        // Act
        districtBranchMapping.setDeleted(true);

        // Assert
        assertTrue(districtBranchMapping.isDeleted());
    }

    /**
     * Method under test: {@link DistrictBranchMapping#setDeleted(boolean)}
     */
    @Test
    void testSetDeleted2() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();
        districtBranchMapping.setBlockID(1);
        districtBranchMapping.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        districtBranchMapping.setCreatedDate(mock(Timestamp.class));
        districtBranchMapping.setDeleted(true);
        districtBranchMapping.setDistrictBranchID(1);
        districtBranchMapping.setHabitat("Habitat");
        districtBranchMapping.setLastModDate(mock(Timestamp.class));
        districtBranchMapping.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        districtBranchMapping.setPanchayatName("Panchayat Name");
        districtBranchMapping.setPinCode("Pin Code");
        districtBranchMapping.setVillageName("Village Name");

        // Act
        districtBranchMapping.setDeleted(true);

        // Assert
        assertTrue(districtBranchMapping.isDeleted());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link DistrictBranchMapping#setCreatedBy(String)}
     *   <li>{@link DistrictBranchMapping#setCreatedDate(Timestamp)}
     *   <li>{@link DistrictBranchMapping#setHabitat(String)}
     *   <li>{@link DistrictBranchMapping#setLastModDate(Timestamp)}
     *   <li>{@link DistrictBranchMapping#setModifiedBy(String)}
     *   <li>{@link DistrictBranchMapping#setPanchayatName(String)}
     *   <li>{@link DistrictBranchMapping#setPinCode(String)}
     *   <li>{@link DistrictBranchMapping#setVillageName(String)}
     *   <li>{@link DistrictBranchMapping#toString()}
     *   <li>{@link DistrictBranchMapping#getBlockID()}
     *   <li>{@link DistrictBranchMapping#getCreatedBy()}
     *   <li>{@link DistrictBranchMapping#getCreatedDate()}
     *   <li>{@link DistrictBranchMapping#getDistrictBranchID()}
     *   <li>{@link DistrictBranchMapping#getHabitat()}
     *   <li>{@link DistrictBranchMapping#getLastModDate()}
     *   <li>{@link DistrictBranchMapping#getModifiedBy()}
     *   <li>{@link DistrictBranchMapping#getPanchayatName()}
     *   <li>{@link DistrictBranchMapping#getPinCode()}
     *   <li>{@link DistrictBranchMapping#getVillageName()}
     *   <li>{@link DistrictBranchMapping#isDeleted()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        DistrictBranchMapping districtBranchMapping = new DistrictBranchMapping();

        // Act
        districtBranchMapping.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Timestamp createdDate = mock(Timestamp.class);
        districtBranchMapping.setCreatedDate(createdDate);
        districtBranchMapping.setHabitat("Habitat");
        Timestamp lastModDate = mock(Timestamp.class);
        districtBranchMapping.setLastModDate(lastModDate);
        districtBranchMapping.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        districtBranchMapping.setPanchayatName("Panchayat Name");
        districtBranchMapping.setPinCode("Pin Code");
        districtBranchMapping.setVillageName("Village Name");
        String actualToStringResult = districtBranchMapping.toString();
        districtBranchMapping.getBlockID();
        String actualCreatedBy = districtBranchMapping.getCreatedBy();
        Timestamp actualCreatedDate = districtBranchMapping.getCreatedDate();
        districtBranchMapping.getDistrictBranchID();
        String actualHabitat = districtBranchMapping.getHabitat();
        Timestamp actualLastModDate = districtBranchMapping.getLastModDate();
        String actualModifiedBy = districtBranchMapping.getModifiedBy();
        String actualPanchayatName = districtBranchMapping.getPanchayatName();
        String actualPinCode = districtBranchMapping.getPinCode();
        String actualVillageName = districtBranchMapping.getVillageName();
        districtBranchMapping.isDeleted();

        // Assert that nothing has changed
        assertEquals("Habitat", actualHabitat);
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualCreatedBy);
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualModifiedBy);
        assertEquals("Panchayat Name", actualPanchayatName);
        assertEquals("Pin Code", actualPinCode);
        assertEquals("Village Name", actualVillageName);
        assertEquals(
                "{\"districtBranchID\":null,\"blockID\":null,\"panchayatName\":\"Panchayat Name\",\"villageName\":\"Village"
                        + " Name\",\"habitat\":\"Habitat\",\"pinCode\":\"Pin Code\",\"deleted\":null,\"createdBy\":\"Jan 1, 2020 8:00am"
                        + " GMT+0100\"}",
                actualToStringResult);
        assertSame(createdDate, actualCreatedDate);
        assertSame(lastModDate, actualLastModDate);
    }

    /**
     * Method under test: {@link DistrictBranchMapping#DistrictBranchMapping()}
     */
    @Test
    void testNewDistrictBranchMapping() {
        // Arrange and Act
        DistrictBranchMapping actualDistrictBranchMapping = new DistrictBranchMapping();

        // Assert
        assertNull(actualDistrictBranchMapping.isDeleted());
        assertNull(actualDistrictBranchMapping.getBlockID());
        assertNull(actualDistrictBranchMapping.getDistrictBranchID());
        assertNull(actualDistrictBranchMapping.getCreatedBy());
        assertNull(actualDistrictBranchMapping.getHabitat());
        assertNull(actualDistrictBranchMapping.getModifiedBy());
        assertNull(actualDistrictBranchMapping.getPanchayatName());
        assertNull(actualDistrictBranchMapping.getPinCode());
        assertNull(actualDistrictBranchMapping.getVillageName());
        assertNull(actualDistrictBranchMapping.getCreatedDate());
        assertNull(actualDistrictBranchMapping.getLastModDate());
    }

    /**
     * Method under test:
     * {@link DistrictBranchMapping#DistrictBranchMapping(Integer, String)}
     */
    @Test
    void testNewDistrictBranchMapping2() {
        // Arrange and Act
        DistrictBranchMapping actualDistrictBranchMapping = new DistrictBranchMapping(1, "Village Name");

        // Assert
        assertEquals("Village Name", actualDistrictBranchMapping.getVillageName());
        assertEquals(1, actualDistrictBranchMapping.getDistrictBranchID().intValue());
    }

    /**
     * Method under test:
     * {@link DistrictBranchMapping#DistrictBranchMapping(Integer, String, String, String, String)}
     */
    @Test
    void testNewDistrictBranchMapping3() {
        // Arrange and Act
        DistrictBranchMapping actualDistrictBranchMapping = new DistrictBranchMapping(1, "Village Name", "Panchayat Name",
                "Habitat", "Pin Code");

        // Assert
        assertEquals("Habitat", actualDistrictBranchMapping.getHabitat());
        assertEquals("Panchayat Name", actualDistrictBranchMapping.getPanchayatName());
        assertEquals("Pin Code", actualDistrictBranchMapping.getPinCode());
        assertEquals("Village Name", actualDistrictBranchMapping.getVillageName());
        assertEquals(1, actualDistrictBranchMapping.getDistrictBranchID().intValue());
    }
}
