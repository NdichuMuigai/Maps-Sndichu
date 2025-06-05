# maps-jhaskell-sndichu

## Overview
The Maps project is an interactive web application that integrates Mapbox for front-end mapping functionalities and a robust back-end for GeoJSON data processing and management. This project was collaboratively developed by Jakobi and Stanley, with an emphasis on providing a seamless experience for overlaying and querying geographic data.

- **Project Name**: Maps
- **Developers**: Jakobi and Stanley
- **CS Logins**: jhaskell, sndichu
- **Development Hours**: 18
- [**Githunk link**](https://github.com/cs0320-f23/maps-jhaskell-sndichu.git)

## Setup Instructions

### Prerequisites
- Node.js and npm
- Java JDK 17

### Frontend Setup
1. **Clone the Repository**: Clone this repository to your local machine.
   
2. **Install Dependencies**: Navigate to the frontend directory and run `npm install` to install the necessary Node.js dependencies.

3. **Environment Variables**: Create a `.env` file in the root of the frontend directory. Specify your Mapbox API key as follows:
   ```
   VITE_MAPBOX_ACCESS_TOKEN=Your_Mapbox_Access_Token_Here
   ```

4. **Start the Frontend**: Run `npm run dev` to launch the frontend server. It should automatically open in your web browser on port 5173.

### Backend Setup
1. **Backend Server**: Navigate to the backend directory in a separate terminal window.

2. **Compile and Run**: Open the pom.xml as a project and use the appropriate Java build tool commands (e.g., `mvn compile` for Maven) to compile and start the backend server. Then run the Server file from IntelliJ. 

3. **GeoJSON Data**: Place your GeoJSON files in the designated directory within the backend structure for them to be accessible by the server.

## Features and Functionalities

### GeoJSON Overlay
- The backend is designed to handle GeoJSON data, allowing users to overlay geographical data on the Mapbox map.

### Keyword Filtering
- The frontend can query the backend using the `filterjson` endpoint. This endpoint accepts parameters for keywords (`keyword`), as well as geographic bounding box coordinates (`minLat`, `minLng`, `maxLat`, `maxLng`).
- Users can filter displayed GeoJSON data based on these parameters.

### Search History
- The server maintains a keyword search history for each session. It stores this history in a file under the `history` folder. 

### Session Management
- The server creates a new session for each client to persist state. This approach ensures that each user's interactions are isolated and managed effectively, enhancing both usability and security.

### Whose Labour? Writeup

Our Maps project is a testament to the collaborative effort of numerous developers, tools, and technologies that span across various domains of software development. At its core, the application leverages the capabilities of both front-end and back-end technologies, each contributing significantly to the final product.

On the front-end, we extensively utilized Mapbox, a powerful mapping service known for its versatility and ease of integration. The labor of Mapbox's developers has been instrumental in allowing us to present interactive and detailed maps, enhancing the user experience substantially. Alongside Mapbox, React played a pivotal role in building the user interface. React's component-based architecture, developed and maintained by the engineers at Facebook, enabled us to create a dynamic and responsive web application efficiently. Additionally, TypeScript, a superset of JavaScript developed by Microsoft, brought type safety and improved code maintainability to our project. The labor of the TypeScript developers has been invaluable in reducing bugs and enhancing the development experience.

On the back-end, Mockito, a popular Java mocking framework, was essential for creating robust and reliable unit tests. Mockito's ease of use for mocking dependencies in our tests allowed us to simulate various scenarios and ensure the stability of our back-end services. The labor of the Mockito contributors has thus been critical in maintaining the quality and reliability of our application. Furthermore, the Google Gson library played a crucial role in our project by enabling the conversion of GeoJSON data into Java objects. The work done by the developers of the Gson library significantly simplified the process of handling and manipulating JSON data within our Java-based back-end.

Other key components and tools that contributed to our project include the Java Development Kit (JDK), the foundational platform for our back-end development, and Node.js, which facilitated our front-end development environment. The Apache Maven build automation tool streamlined our back-end build process, while npm managed our front-end dependencies. The integration of these tools reflects the collective effort and innovation of countless developers and contributors worldwide, whose work has directly impacted the success of our Maps project.

Each of these components, from the Mapbox service to the TypeScript language, represents a culmination of expertise, dedication, and continuous development by professionals and open-source contributors. Their labor has not only made our project possible but also allowed us to focus on creating a product that is both functional and user-friendly. As we run our capstone demo, we are reminded of the vast ecosystem we rely upon and are grateful for the collective efforts that have shaped the tools and technologies at our disposal.