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
import {
    AppBar,
    Button,
    Container,
    Divider,
    Drawer,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Toolbar
} from "@mui/material";
import UserInfo from "./User/UserInfo";
import SurveyMenu from "./Pages/Survey/SurveyMenu";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import * as Space from 'react-spaces';
import DeleteIcon from '@mui/icons-material/Delete';
import HomeIcon from '@mui/icons-material/Home';

// TODO: prevent users from going through the site by entering paths in the url
// TODO: currently saving everything in local storage but IT IS NOT SAFE


function App(){

    const barWidth = "10%";
    const sidebarWidth = "15%";
    const page_does_not_exist_string = "דף זה אינו קיים";
    const logout_button_string = "יציאה";


    const drawer = (
        <Space.Fill>
            <Space.Top size={barWidth}>
                {/*TODO: make a logo*/}
                <h1>מענ"ה</h1>
            </Space.Top>
            <Space.Fill>
                <Divider/>
                {/*TODO: add onclick to the list buttons*/}
                <List>
                    <ListItem button>
                        <ListItemIcon>
                            <HomeIcon/>
                        </ListItemIcon>
                        <ListItemText primary="בית"/>
                    </ListItem>
                </List>
            </Space.Fill>
        </Space.Fill>
    );

    return (
        <div dir="rtl">
            <Space.ViewPort >
                <Space.Right size={sidebarWidth}>
                        <Drawer
                            sx={{
                                width: sidebarWidth,
                                flexShrink: 0,
                                '& .MuiDrawer-paper': {
                                    width: sidebarWidth,
                                    boxSizing: 'border-box',
                                },
                            }}
                            variant="persistent"
                            anchor="left"
                            open
                        >
                            {drawer}
                        </Drawer>
                </Space.Right>
                <Space.Fill>
                    <Space.Top size={barWidth}>
                        {/* TODO: fix it so the card line would be see and it would align with the logo*/}
                        {/* TODO:  can put an avatar and say hello <name> or something*/}
                        <AppBar style={{minHeight: "99%"}}  color="background" position="static">
                            <Toolbar>
                                {/* TODO: see what to put instead of this typography*/}

                                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                                    News
                                </Typography>
                                {/*TODO: set an onclick for the button*/}
                                <Button color="inherit">{logout_button_string}</Button>
                            </Toolbar>
                        </AppBar>
                    </Space.Top>
                    <Space.Fill centerContent="horizontalVertical">
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
                    </Space.Fill>
                </Space.Fill>
            </Space.ViewPort>
        </div>
    )
}

export default App;
































// function App() {
//
//     const barWidth = "10%"
//
//     const drawer_list =
//         <Space.Fill>
//             <h2>מענ"ה</h2>
//             <Divider/>
//             <List>
//                 <ListItem button onClick={() => document.location.href = window.location.origin + '/user/workPlan'}>
//                     <ListItemText primary="בית" />
//                 </ListItem>
//             </List>
//             {window.sessionStorage.getItem('type') === "INSTRUCTOR" &&
//                 <List>
//                     <ListItem button onClick={() => document.location.href = window.location.origin + '/user/workPlan'}>
//                         <ListItemText primary="בית" />
//                     </ListItem>
//                 </List>}
//
//             {(window.sessionStorage.getItem('type') === "SUPERVISOR" || window.sessionStorage.getItem('type') === "SYSTEM_MANAGER") &&
//             <List>
//                 <ListItem button onClick={() => document.location.href = window.location.origin + '/user/manageUsers'}>
//                     <ListItemText primary="בית" />
//                 </ListItem>
//                 <ListItem button onClick={() => document.location.href = window.location.origin + '/survey'}>
//                     <ListItemText primary="סקרים" />
//                 </ListItem>
//             </List>}
//
//             {window.sessionStorage.getItem('type') === "GENERAL_SUPERVISOR" &&
//             <List>
//                 <ListItem button onClick={() => document.location.href = window.location.origin + '/user/InfoViewer'}>
//                     <ListItemText primary="בית" />
//                 </ListItem>
//             </List>}
//         </Space.Fill>
//
//   const page_does_not_exist_string = "דף זה אינו קיים"
//   return (
//     <Space.ViewPort dir="rtl" className="App">
//         {!document.location.href.includes("login") && // TODO: a better case would be if the user is logged in
//             <Space.Right size="20%">
//                 <Space.Top size={barWidth}>
//                     <h1>מענ"ה</h1>
//                 </Space.Top>
//                 <Space.Fill>
//                     <Drawer
//                         variant="permanent"
//                         ModalProps={{
//                             keepMounted: true,
//                         }}
//                     >
//                         <List>
//                             {['Inbox', 'Starred', 'Send email', 'Drafts'].map((text, index) => (
//                                 <ListItem button key={text}>
//                                     <ListItemIcon>
//                                         <DeleteIcon />
//                                     </ListItemIcon>
//                                     <ListItemText primary={text} />
//                                 </ListItem>
//                             ))}
//                         </List>
//                     </Drawer>
//                 </Space.Fill>
//             </Space.Right>
//         }
//         <Space.Fill>
//             <Space.Top centerContent="vertical" size={barWidth}>
//                 <AppBar color="background">
//                     <Toolbar variant="dense">
//                         <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
//                             {/*    TODO: if i remove this empty thing then everything in the app bar vanishes*/}
//                         </Typography>
//                         {/*TODO: implement logout*/}
//                         <Button color="inherit">יציאה</Button>
//                     </Toolbar>
//                 </AppBar>
//             </Space.Top>
//             <Space.Fill centerContent="vertical">
//                 <Routes>
//                     <Route path="user/login" element={<Login/>}/>
//                     <Route path="user/registerUsers" element={<RegisterUsers/>}/>
//                     <Route path="user/workPlan" element={<WorkPlan/>}/>
//                     <Route path="user/manageUsers" element={<ManageUsers/>}/>
//                     <Route path="user/InfoViewer" element={<InfoViewer/>}/>
//                     <Route exact path="/survey" element={<SurveyMenu />}/>
//                     <Route path="survey/createSurvey" element={<SurveyBuilder/>}/>
//                     {/*    TODO: change to survey/getSurvey=id (the one below)*/}
//                     <Route path="survey/survey" element={<Survey/>}/>
//                     <Route
//                         path="*"
//                         element={
//                             <main style={{ padding: "1rem" }}> {/*todo: make this page prettier */}
//                                 <p>{page_does_not_exist_string}</p>
//                             </main>
//                         }
//                     />
//                 </Routes>
//             </Space.Fill>
//         </Space.Fill>
//     </Space.ViewPort>
//   );
// }

