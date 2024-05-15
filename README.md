The TIW-RIA project, hosted on GitHub, is a web application developed as part of the "Tecnologie Informatiche per il Web" course at Politecnico di Milano. The repository contains implementations in both Rich Internet Application (RIA) and pure HTML variants, supported by a Java servlet-based backend.

### Key Features

1. **Backend Development**:
   - The backend is developed using Java Servlets.
   - It communicates with a MySQL database through JDBC.
   - The project is structured to handle typical web app functionalities, including user authentication, product management, and order processing.

2. **Frontend Development**:
   - The front end is built using HTML, CSS, and JavaScript.
   - AJAX is used for asynchronous operations to enhance the user experience by reducing page reloads.
   - The interface includes dynamic elements like popups and real-time updates, making the app interactive and responsive.

3. **Project Structure**:
   - The `src` directory contains the main application source code.
   - The `WebContent` directory holds the web resources such as HTML, CSS, and JavaScript files.
   - A SQL dump file (`db_dump.sql`) is provided for setting up the database.

4. **Development Tools**:
   - The project uses Maven for build and dependency management, with configurations specified in the `pom.xml` file.
   - IntelliJ IDEA is recommended as the IDE for development, as indicated by the project files and settings included.

### Setup and Usage

1. **Clone the Repository**:
   ```sh
   git clone https://github.com/MatteoGobbiF/TIW-RIA.git
   cd TIW-RIA
   ```

2. **Database Setup**:
   - Import the provided `db_dump.sql` into your MySQL database to set up the necessary tables and data.

3. **Build and Run the Application**:
   - Use Maven to build the project:
     ```sh
     mvn clean install
     ```
   - Deploy the application on a Tomcat server:
     ```sh
     mvn tomcat7:run
     ```
   - Access the application at `http://localhost:8080`.

### Interface Overview

- **Access Panel Form**: The initial login or registration page.
- **Home Page**: Displays promoted or recently viewed products.
- **Product Search**: Allows users to search for products and view results dynamically.
- **Product Details Popup**: Shows detailed information about a product when hovered over.
- **Cart**: Manages the products the user intends to purchase.
- **Orders**: Displays past and current orders of the user.

For more detailed documentation, refer to the `Documentation_RIA.pdf` file included in the repository.
