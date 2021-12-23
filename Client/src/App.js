import './App.css';
import { Routes, Route } from "react-router-dom";
import Login from "./Pages/Credentials/Login";
import React from "react";
import SurveyBuilder from "./Pages/SurveyBuilder/SurveyBuilder";
import RegisterUsers from "./Pages/Credentials/RegisterUsers";
import Survey from "./Pages/Survey/Survey";
import WorkPlan from "./Pages/WorkPlan/WorkPlan";
import ManageUsers from "./Pages/ManageUsers/ManageUsers";
import Connection from "./Communication/Connection.js"
import InfoViewer from "./Pages/GeneralSupervisorInfoViewer/InfoViewer";
import {Divider, Drawer, List, ListItem, ListItemText} from "@mui/material";
import UserInfo from "./User/UserInfo";
import SurveyMenu from "./Pages/Survey/SurveyMenu";

// TODO: prevent users from going through the site by entering paths in the url
// TODO: currently saving everything in local storage but IT IS NOT SAFE

function App() {

    const drawer_list =
        <div>
            <h2>מענ"ה</h2>
            <Divider/>
            {window.sessionStorage.getItem('type') === "INSTRUCTOR" &&
                <List>
                    <ListItem button onClick={() => document.location.href = window.location.origin + '/user/workPlan'}>
                        <ListItemText primary="בית" />
                    </ListItem>
                </List>}

            {(window.sessionStorage.getItem('type') === "SUPERVISOR" || window.sessionStorage.getItem('type') === "SYSTEM_MANAGER") &&
            <List>
                <ListItem button onClick={() => document.location.href = window.location.origin + '/user/manageUsers'}>
                    <ListItemText primary="בית" />
                </ListItem>
                <ListItem button onClick={() => document.location.href = window.location.origin + '/survey'}>
                    <ListItemText primary="סקרים" />
                </ListItem>
            </List>}

            {window.sessionStorage.getItem('type') === "GENERAL_SUPERVISOR" &&
            <List>
                <ListItem button onClick={() => document.location.href = window.location.origin + '/user/InfoViewer'}>
                    <ListItemText primary="בית" />
                </ListItem>
            </List>}
        </div>

  const page_does_not_exist_string = "דף זה אינו קיים"
  return (
    <div dir="rtl" className="App">
        <div>
            {!document.location.href.includes("login") && <Drawer
                variant="permanent"
                ModalProps={{
                    keepMounted: true,
                }}
            >
                {drawer_list}
            </Drawer>}
        </div>
        <div>
            <Routes>
                <Route path="user/login" element={<Login/>}/>
                <Route path="user/registerUsers" element={<RegisterUsers/>}/>
                <Route path="user/workPlan" element={<WorkPlan/>}/>
                <Route path="user/manageUsers" element={<ManageUsers/>}/>
                <Route path="user/InfoViewer" element={<InfoViewer/>}/>
                <Route exact path="/survey" element={<SurveyMenu />}/>
                <Route path="survey/createSurvey" element={<SurveyBuilder/>}/>
                {/*    TODO: change to survey/getSurvey=id (the one below)*/}
                <Route path="survey/survey" element={<Survey/>}/>
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
    </div>
  );
}

export default App;