
## Assignment Requirements

### 1) Part One

Prepare a report about Java Sprint Boot and Node.js frameworks for the modern enterprise web technology to build scalable, secure, high-performance web applications for large organizations. Your report should cover the following:
- The two architecture frameworks in detail
- The AI integration and orchestration in detail
- The REST-based architecture in detail

Include suitable diagrams and code snippets in your report to justify your choices. (Approximately 3000 words)

### 2) Part Two

This assignment is to design and develop a web-based Course Recovery System (CRS) to be presented to the higher education institutions. Refer to the scenario and specifications.

There are 2 types of end users interacting to the required system:
i. Course Administrator
ii. Academic Officer

The application is to be developed in a multi-tier architecture with each tier to be implemented using the technologies defined as follows:

*   **The presentation tier** is a web application based on JSP/JSF technologies.
    *   The JSPs may contain Standard Tag Library functions and Servlet may be used for validating input fields with predefined string and/or number format.
    *   The JSFs may utilize PrimeFaces UI components for an intuitive design.
*   **The business tier** is an application that contains the business logic and processes with connections to the backend database via JDBC using Enterprise Java Beans (EJB).
*   **The database tier** is a database application used to store all relevant data of the application.



## Scenario:

Course Recovery System (CRS) is a software solution that is designed to help educational institutions to manage how the students recover failed or incomplete courses. It ensures that the students can resume on track academically without delaying graduation. The key purpose of the solution are to allow students to enrol in recovery process to improve academic profiles and also allow course administrators and instructors to plan, track and evaluate course recovery plan. To streamline this academic study workflow, a Java-based web application must be developed using Enterprise Java technology. The system should offer the functional requirements as stated in the section “Functional Requirements”.

## Functional requirements:

### 1) User Management
This is a process of controlling and maintaining users’ accounts and accessibility and interaction with the system. The following features are commonly included, but not limited to.
*   User accounts (Add/Update/Deactivate)
*   Authentication and Authorization
    *   Login/Logout
    *   Role-based permission enforcement or access control
*   Password and Credential Management (Reset/Recover)

### 2) Course Recovery Plan
This is a special strategy process that allows students to improve weak performance, through recovering the failed modules with specified timeline and deadlines. The following features are commonly included, but not limited to. Example,
*   List out all failed components of the course i.e., assignment or exam for the student concerned
*   Recommendation entry for the course recovery (Add/Update/Remove)
*   Set a clear milestone for course recovery with the action plan (Add/Update/Remove)
*   Monitor/ Track the Recovery progress inclusive the grading entry

| COURSE STUDY | WEEK | TASK |
| :--- | :--- | :--- |
| Object Oriented Programming | Week 1 – 2 | Review all lecture topic |
| | Week 3 | Meeting with module lecturer |
| | Week 4 | Take recovery exam |



### 3) Eligibility Check and Enrolment
This is a process of checking whether the students meet the required criteria to join the next level of study. The following features are commonly included, but not limited to.
*   List out all students who are not eligible to progress into next level of study. The two eligibility criteria of progression as follows:
    *   At least CGPA 2.0, the Credit Hour System as such
        `CGPA = (Total Grade Points of all courses taken) / (Total Credit Hours of all courses taken)`
        Computation sample:
        *   Course 1: A (4.0) × 3 credits = 12
        *   Course 2: B (3.0) × 4 credits = 12
        *   Course 3: C (2.0) × 2 credits = 4
        *   Total = 28 / 9 = 3.11 CGPA
    *   Not more than three failed courses to be allowed to progress into next level of study
*   To allow registration once the eligibility is confirmed.

### 4) Academic Performance Reporting
This is the formal document that provides a summary of a student’s progress, achievements, and the recommendations of improvement over a specific period. The following features are commonly included, but not limited to.
*   Generate an academic performance report by semester and year of study
*   Example:

**Student Name:** Alex Tan
**Student ID:** 2025A1234
**Program:** Bachelor of Computer Science

**Semester 1**

| Course Code | Course Title | Credit Hours | Grade | Grade Point (4.0 Scale) |
| :--- | :--- | :--- | :--- | :--- |
| CS201 | Data Structures | 3 | A | 4.0 |
| CS205 | Database Systems | 3 | B+ | 3.3 |
| CS210 | Software Engineering I | 3 | B | 3.0 |
| MA202 | Discrete Mathematics | 4 | C+ | 2.3 |
| EN201 | Academic Writing | 2 | A- | 3.7 |
| | **Cumulative GPA (CGPA):** | | | **3.25** |



### 5) Email Notifications
This is an automated messages send by a system to the users i.e., student’s email address. It helps maintain communication message as alerts, reminders, confirmations and etc between the system and its users to improve accountability. The following are the applicable areas for this service, but not limited to.
*   User account management
*   Password & recovery management
*   Course recovery with a clear action plan and milestone
*   Academic performance report

The following are the additional descriptions for better understanding of the academic study system.

**Overview Use Case Diagram:**
*(Note: Diagram would be present in original document)*

**Course Retrieval Policy:**
*   Each course has three attempt opportunities to pass the course only.
*   The failed component is required only to pass by resubmission or resit in 2nd attempt.
*   If the students cannot pass the course in the 1st and 2nd attempts, the 3rd attempt requires the students to refer to all assessment components.



## Assignment Deliverables

### 1) Part One
The deliverable should consist of an evaluation report comprising below contents.
*   Brief history on distributed computing and discussion on architectural evolution for the business enterprises.
*   Discussion on web application frameworks, AI services integration and REST-based architecture roles in the distributed environment.

### 2) Part Two
The deliverable consists of a design document, source code, and deployable modules/application. The design document should describe the system architecture, components, and customized component frameworks. The deliverable as a single JEE project is to be uploaded to Moodle on or before the project due date (Refer to the deadline at Moodle). Your design documents should include the following content:
*   A cover page and Table of contents (with page numbering).
*   Design of web components with a brief description of how the web component technologies are used (JSPs / JSFs, Servlets).
*   Web page design, including a general navigation chart of all pages.
*   Design of business tier with a brief description of how the technologies are used (EJBs).
*   Overview of your application and a brief description of the system architecture (and UML diagrams) and interconnection among the tiers.
*   Database design, with description of each table and an E-R diagram, or a domain diagram to describe the relationships. Design of database access APIs.
*   References

#### 2.1 Knowledge/Presentation
*   Able to provide all information and / or answer all questions with regards to the component of the project.
*   Answers questions accurately and confidently.

#### 2.2 Suggested Development Steps:
*   It is recommended that you follow the standard software development process, from analysis to design, then to implementation and testing. A good start would be trying to understand the application requirement and to layout the web pages and their relationship to JSFs/JSPs, and data entities. A good design would make your implementation much easier.
*   You need to divide the application into components according to their tiers. Once you have a thorough design completed and the interface among these components defined, you may proceed to implement and test each component one at a time, without having them interact with each other. Once you have each component fully implemented and tested, you can proceed with the integration.
*   It is recommended to complete your design, and then start implementing your system based on your design (not vice versa). If you found out during the implementation stage that something is wrong with your design, go back to your documentation and correct it before continuing with your implementation. Update your design document as you redesign and code.
*   Before delivering the final package, please test the ear files and database files on another machine or your own, or simply delete your existing application and database files, then drop the ear file into the auto-deploy directory, and the database files into the database directory of the application server, and see if everything would deploy and work the same as before.



#### 2.3 Software Required
*   Java Development Kit (JDK) 17 or above
*   Java Enterprise Edition (JEE)
*   Database, MySQL / Java DB

**Instructions:**
This is a Group assignment (2 or 3 students only). Upon submission of your assignment, you would be required to present your assignment at a date and time specified by your module lecturer.



## MARKING CRITERIA
*   Evaluation Report (20 %)
*   Implementation (50 %)
*   Design & Program Documentation (20 %)
*   Knowledge/Presentation (10 %)

**(Total: 100 Marks)**

## GRADING CRITERIA

| MARKING KEY | EQUIVALENT MARKS |
| :--- | :--- |
| **A+ = Distinction** A+ | **80 - 100**<br>Superior achievement in assignment, outstanding quality; complete in every way. |
| **A = Distinction** A | **75 - 79**<br>Very high achievement in all objectives, excellent quality assignment. |
| **B+ = Credit** B+ | **70 - 74**<br>Very good/High achievement in most objectives, high quality assignment. |
| **B = Credit** B | **65 - 69**<br>Good/High achievement in most objectives, shows some of the qualities but lacks comprehensiveness nevertheless quality assignment. |
| **C = Pass** C+/C/C- | **50 - 64**<br>Satisfactory/competent achievement in most objectives, all essential points covered plus some of the minor ones. |
| **F = Marginal Fail / Fail** D/F+/F/F- | **below 49**<br>Unsatisfactory, Improvement essential/poor achievement; poor quality assignment, some essential objectives not covered. |
