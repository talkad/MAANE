import './App.css';
import { Routes, Route } from "react-router-dom";
import Login from "./Pages/Credentials/Login";
import React from "react";
import SurveyBuilder from "./Pages/SurveyBuilder/SurveyBuilder";
import RegisterUsers from "./Pages/Credentials/RegisterUsers";
import Survey from "./Pages/Survey/Survey";
import WorkPlan from "./Pages/WorkPlan/WorkPlan";
import ManageUsers from "./Pages/ManageUsers/ManageUsers";

// TODO: prevent users from going through the site by entering paths in the url

function App() {

  const page_does_not_exist_string = "דף זה אינו קיים"
  return (
    <div dir="rtl" className="App">
      <Routes>
          <Route path="/" element={<Login/>}/>
          <Route path="registerUsers" element={<RegisterUsers/>}/>
          <Route path="createSurvey" element={<SurveyBuilder/>}/>
          <Route path="survey" element={<Survey/>}/>
          <Route path="workPlan" element={<WorkPlan/>}/>
          <Route path="manageUsers" element={<ManageUsers/>}/>
          <Route
              path="*"
              element={
                  <main style={{ padding: "1rem" }}> {/*todo: make this page prettier */}
                      <p>{page_does_not_exist_string}</p>
                  </main>
              }
          />
      </Routes>
    </div>
  );
}

export default App;
