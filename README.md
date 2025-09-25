# AMRIT - Telemedicine (TM) Service

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![Build Status](https://github.com/PSMRI/TM-API/actions/workflows/sast-and-package.yml/badge.svg)

The AMRIT Telemedicine (TM) Service enhances the capabilities of Health and Wellness Centers (HWCs) by providing remote healthcare services, improving accessibility, enabling collaborative care, and integrating with other facilities such as drug dispensing and laboratory services. This service aims to extend the reach and convenience of HWCs, ensuring that patients receive necessary medical advice and services without the need for in-person visits.

## Features of Telemedicine (TM)

### **Telemedicine in Health and Wellness Centers (HWCs)**

Telemedicine is integral to HWCs, enhancing accessibility and efficiency in healthcare services.

1. **Teleconsultation** – HWCs use telemedicine for virtual consultations, allowing patients to connect with doctors via video calls for diagnoses, medical advice, and follow-ups, reducing the need for in-person visits.

2. **Improved Accessibility** – Patients in remote areas or with mobility issues can access healthcare conveniently without traveling to HWCs.

3. **Outpatient Services** – Telemedicine supports outpatient care by enabling virtual visits, follow-ups, and remote monitoring, ensuring timely medical attention without physical appointments.

4. **Medication Management** – Doctors can remotely prescribe medications, and patients can collect them from the HWC’s drug dispensing facility, ensuring a smooth medication process.

5. **Collaborative Care** – HWCs can consult specialists via telemedicine for expert opinions, ensuring comprehensive patient care through seamless collaboration.

6. **Integration with Laboratory Services** – IoT-enabled labs can transmit test data directly to the HWC’s application, facilitating real-time consultations and efficient interpretation of results.

Telemedicine strengthens HWCs by bridging geographical gaps, optimizing resources, and ensuring timely medical intervention.


## Building From Source

This microservice is built using Java and the Spring Boot framework, with MySQL as the underlying database. Before building the TM module, ensure you have the following prerequisites:
For step-by-step guide, follow this [guide] (https://piramal-swasthya.gitbook.io/amrit/developer-guide/development-environment-setup) .

## Prerequisites
- JDK 17 (LTS)
- Maven

To build the TM module from source, follow these steps:

1. Clone the repository to your local machine.
2. Install the required dependencies and build the module using the following command:
- Execute the following command:
  ```
  mvn clean install
  ```
3.  You can copy `common_example.properties` to `common_local.properties` and edit the file accordingly. The file is under `src/main/environment` folder.
4. Run the development server by following these steps:
- Start the Redis server.
- Execute the following command:
  ```
  mvn spring-boot:run -DENV_VAR=local
  ```
- Open your browser and navigate to http://localhost:8080/swagger-ui.html#!/

## Setting Up Commit Hooks

This project uses Git hooks to enforce consistent code quality and commit message standards. Even though this is a Java project, the hooks are powered by Node.js. Follow these steps to set up the hooks locally:

### Prerequisites
- Node.js (v14 or later)
- npm (comes with Node.js)

### Setup Steps

1. **Install Node.js and npm**
   - Download and install from [nodejs.org](https://nodejs.org/)
   - Verify installation with:
     ```
     node --version
     npm --version
     ```
2. **Install dependencies**
   - From the project root directory, run:
     ```
     npm ci
     ```
   - This will install all required dependencies including Husky and commitlint
3. **Verify hooks installation**
   - The hooks should be automatically installed by Husky
   - You can verify by checking if the `.husky` directory contains executable hooks
### Commit Message Convention
This project follows a specific commit message format:
- Format: `type(scope): subject`
- Example: `feat(login): add remember me functionality`
Types include:
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code changes that neither fix bugs nor add features
- `perf`: Performance improvements
- `test`: Adding or fixing tests
- `build`: Changes to build process or tools
- `ci`: Changes to CI configuration
- `chore`: Other changes (e.g., maintenance tasks, dependencies)
Your commit messages will be automatically validated when you commit, ensuring project consistency.

## Usage

All the features of the TM module have been exposed as REST endpoints. For detailed information on how to use the service, refer to the SWAGGER API specification.

With the TM module, you can efficiently manage all aspects of your telemedicine application, ensuring seamless remote healthcare services for patients and collaboration among healthcare professionals.

## Filing Issues

If you encounter any issues, bugs, or have feature requests, please file them in the [main AMRIT repository](https://github.com/PSMRI/AMRIT/issues). Centralizing all feedback helps us streamline improvements and address concerns efficiently.  

## Join Our Community

We’d love to have you join our community discussions and get real-time support!  
Join our [Discord server](https://discord.gg/FVQWsf5ENS) to connect with contributors, ask questions, and stay updated.  

