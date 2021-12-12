import './App.css';
import { Routes, Route } from "react-router-dom";
import Login from "./Pages/Login";
import React from "react";
import Survey from "./Pages/Survey";



function App() {
  return (
    <div dir="rtl" className="App">
      <Routes>
          <Route path='/' element={<Login/>}/>
          <Route path='survey' element={<Survey/>}/>
          <Route
              path="*"
              element={
                  <main style={{ padding: "1rem" }}>
                      <p>דף זה אינו קיים</p>
                  </main>
              }
          />
      </Routes>
    </div>
  );
}

export default App;
