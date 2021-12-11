import './App.css';
import { Routes, Route } from "react-router-dom";
import Login from "./Pages/Login";
import React from "react";



function App() {
  return (
    <div dir="rtl" className="App">
      <Routes>
        <Route exact path='/' element={<Login />}/>
      </Routes>
    </div>
  );
}

export default App;
