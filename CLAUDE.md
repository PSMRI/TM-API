# CLAUDE.md - TM-API

## Project Overview

TM-API is the backend service for AMRIT's Telemedicine module. It supports remote clinical consultations including the full clinical workflow (registration, nurse assessments, doctor consultations, lab, pharmacist), video consultations, foetal monitoring, teleconsultation specialist collaboration, patient app integration, and data synchronization between spoke (field) and hub (specialist) sites.

## Tech Stack

- Java 17, Spring Boot 3.2.2, Spring Security, Spring Data JPA, Hibernate
- MySQL 8.0 (via mysql-connector-j)
- Redis (session management)
- Spring AOP (cross-cutting concerns)
- MapStruct (object mapping), Lombok
- Swagger/OpenAPI (springdoc-openapi)
- WAR packaging for Wildfly deployment
- JaCoCo (coverage), Checkstyle (style)

## Build & Run

```bash
mvn clean install -DENV_VAR=local          # Build
mvn spring-boot:run -DENV_VAR=local        # Run locally
mvn -B package --file pom.xml -P <profile> # Package WAR (dev, local, test, ci, uat)
mvn test                                    # Run tests
```

Environment is set via `-DENV_VAR=<env>` which selects `common_<env>.properties`. Wildfly deployment profiles configured with hostname/port properties for test, dev, and UAT environments.

## Package Structure

Base package: `com.iemr.tm`

| Package | Purpose |
|---------|---------|
| `controller/` | REST controllers organized by clinical role/visit type |
| `service/` | Business logic layer |
| `repo/` | JPA repositories |
| `data/` | JPA entity classes + master data entities |
| `config/` | Spring configuration (includes Spring Security) |
| `utils/` | Cross-cutting: Redis, HTTP clients, validators, response wrappers |

## Key Domains / Controllers

### Clinical Workflow (mirrors MMU-API for remote consultations)
- **RegistrarController** - Patient registration
- **AntenatalCareController** - ANC visits
- **PostnatalCareController** - PNC visits
- **NCDCareController** - NCD care visits
- **NCDScreeningController** - NCD screening
- **GeneralOPDController** - General OPD visits
- **CancerScreeningController** - Cancer screening
- **CovidController** - COVID-19 screening
- **QuickConsultController** - Quick consultations

### Telemedicine-Specific
- **TeleConsultationController** - Teleconsultation requests, specialist assignments, and responses
- **VideoConsultationController** - Video call session management
- **FoetalMonitorController** - Foetal monitor device integration and data
- **QuickbloxController** - QuickBlox video/chat integration
- **WorklistController** - Worklists for specialists and doctors
- **CRMReportController** - Telemedicine reports

### Support Services
- **AnthropometryVitalsController** - Vitals and anthropometry
- **LabtechnicianController** - Lab test management
- **CommonMasterController** - Shared master data
- **SnomedController** - SNOMED CT terminology
- **LocationController** - Location master data
- **PatientAppCommonMasterController** - Patient-facing app master data
- **IemrMmuLoginController** - Authentication
- **VersionController** - API version info

### Data Sync
- `service/dataSyncActivity/` - Spoke-to-hub data synchronization
- `service/dataSyncLayerCentral/` - Central-side sync processing

## Architecture Notes

- Structurally very similar to MMU-API but adds telemedicine-specific features (video consultation, foetal monitoring, patient app, QuickBlox)
- Spring Security is enabled (unlike MMU-API) for securing telemedicine endpoints
- Hub-and-spoke model: field workers collect data at spoke sites, specialists consult remotely from hub
- Beneficiary flow status tracked in `data/benFlowStatus/` for workflow progression (registrar -> nurse -> doctor -> lab -> pharmacist)
- Extensive master data model under `data/masterdata/` organized by clinical domain
- Validator utilities in `utils/validator/` for request validation
- Session management via Redis (`utils/redis/`)
- Data sync services handle bidirectional sync between local and central databases
