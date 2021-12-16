import './App.css';
import { Routes, Route } from "react-router-dom";
import Login from "./Pages/Login";
import React from "react";
import SurveyBuilder from "./Pages/SurveyBuilder/SurveyBuilder";



function App() {
  return (
    <div dir="rtl" className="App">
      <Routes>
          <Route path='/' element={<Login/>}/>
          <Route path='createSurvey' element={<SurveyBuilder/>}/>
          <Route
              path="*"
              element={
                  <main style={{ padding: "1rem" }}> {/*todo: make this page prettier */}
                      <p>דף זה אינו קיים</p>
                  </main>
              }
          />
      </Routes>
    </div>
  );
}

export default App;
