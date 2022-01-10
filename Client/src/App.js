import './App.css';
import React, {useEffect, useState} from "react";
import { Routes, Route } from "react-router-dom";
import { useNavigate } from "react-router-dom";

// PAGES
import Login from "./Pages/Credentials/Login";
import SurveyBuilder from "./Pages/SurveyBuilder/SurveyBuilder";
import RegisterUsers from "./Pages/Credentials/RegisterUsers";
import Survey from "./Pages/Survey/Survey";
import WorkPlan from "./Pages/WorkPlan/WorkPlan";
import ManageUsers from "./Pages/ManageUsers/ManageUsers";
import InfoViewer from "./Pages/GeneralSupervisorInfoViewer/InfoViewer";
import SurveyConstraintBuilder from "./Pages/SurveyConstraints/SurveyConstraintBuilder";
import GuidingBaskets from "./Pages/GuidingBaskets/GuidingBaskets";
import WorkReport from "./Pages/WorkReport";

// COMPONENTS
import {
    AppBar,
    Button,
    Divider,
    Drawer, IconButton,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Toolbar
} from "@mui/material";
import SurveyMenu from "./Pages/Survey/SurveyMenu";
import * as Space from 'react-spaces';

// ICONS
import HomeIcon from '@mui/icons-material/Home';
import PollIcon from '@mui/icons-material/Poll';
import ShoppingBasketIcon from '@mui/icons-material/ShoppingBasket';
import SummarizeIcon from '@mui/icons-material/Summarize';
import AccountCircle from "@mui/icons-material/AccountCircle";
import Typography from "@mui/material/Typography";
import Connection from "./Communication/Connection";

// TODO: prevent users from going through the site by entering paths in the url
// TODO: currently saving everything in local storage but IT IS NOT SAFE
// TODO: change the usage of document.location.href with the useNavigate hook (example in SurveyMenu.js) in the whole project


function App(){
    const [type, setType] = useState('SUPERVISOR') //TODO: change back to 'GUEST' when not developing

    let navigate = useNavigate();

    //let type = "SUPERVISOR";
    const barWidth = "8%";
    const sidebarWidth = "15%";
    const page_does_not_exist_string = "דף זה אינו קיים";
    const logout_button_string = "יציאה";
    // TODO: the greetings currently doesn't work well. but perhaps once TAL implements what i asked then it will (return the username with the response for the request)
    const greetings_string = "שלום " + window.sessionStorage.getItem('username') // TODO: instead of the username, use the actual name of the user

    const logoutCallback = (data) => {
        setType("GUEST")
        window.sessionStorage.setItem('username', data.result)
        navigate('/user/login', {replace: true})
    }

    const handleLogout = () => {
        console.log("sending logout")
        Connection.getInstance().logout({
            name: window.sessionStorage.getItem('username')
        }, logoutCallback)
    }

    useEffect(() => {
        // TODO: needed?
    }, );

    // sidebar
    const drawer = (
        <Space.Fill>
            <Space.Top size={barWidth}>
                {/*TODO: make a logo*/}
                <h1>מענ"ה</h1>
            </Space.Top>
            <Space.Fill>
                <Divider/>
                {/*TODO: show buttons based on permissions*/}
                <List>
                    <ListItem button onClick={() => navigate(`user/home`, {replace: true})}>
                        <ListItemIcon>
                            <HomeIcon/>
                        </ListItemIcon>
                        <ListItemText primary="בית"/>
                    </ListItem>
                    <ListItem button onClick={() => navigate(`survey/menu`, {replace: true})}>
                        <ListItemIcon>
                            <PollIcon/>
                        </ListItemIcon>
                        <ListItemText primary="סקרים"/>
                    </ListItem>
                    <ListItem button onClick={() => navigate(`user/guidingBasketsSearch`, {replace: true})}>
                        <ListItemIcon>
                            <ShoppingBasketIcon/>
                        </ListItemIcon>
                        <ListItemText primary="סלי הדרכה"/>
                    </ListItem>
                    <ListItem button onClick={() => navigate(`user/workReport`, {replace: true})}>
                        <ListItemIcon>
                            <SummarizeIcon/>
                        </ListItemIcon>
                        <ListItemText primary='דו"ח עבודה'/>
                    </ListItem>
                </List>
            </Space.Fill>
        </Space.Fill>
    );

    return (
        <div dir="rtl">
            {/* TODO:  hide the sidebar and appbar when the user is not logged in*/}
            <Space.ViewPort >
                {/* sidebar */}
                {type !== 'GUEST' && <Space.Right size={sidebarWidth}>
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
                </Space.Right>}
                <Space.Fill>
                    {/* app bar */}
                    {type !== 'GUEST' && <Space.Top size={barWidth}>
                        {/* TODO: fix it so the card line would be see and it would align with the logo*/}
                        <AppBar style={{minHeight: "99%"}}  color="background" position="static">
                            <Toolbar>
                                <IconButton
                                    size="large"
                                    aria-label="account of current user"
                                    aria-controls="menu-appbar"
                                    aria-haspopup="true"
                                    color="inherit"
                                >
                                    <AccountCircle />
                                </IconButton>
                                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                                    {greetings_string}
                                </Typography>
                                <Button onClick={() => handleLogout()} color="inherit">{logout_button_string}</Button>
                            </Toolbar>
                        </AppBar>
                    </Space.Top>}
                    <Space.Fill>
                        {/* routes to the different screens */}
                        <Routes>
                            {/*TODO: find a more elegant way for the permissions*/}
                            <Route path="user">
                                <Route path="login" element={<Login changeType={setType}/>}/>

                                {(type === "SUPERVISOR" || type === "INSTRUCTOR") &&
                                    <Route path="guidingBasketsSearch" element={<GuidingBaskets/>}/>}

                                {(type === "SUPERVISOR" || type === "INSTRUCTOR") &&
                                    <Route path="workReport" element={<WorkReport/>}/>}

                                {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                    <Route path="registerUsers" element={<RegisterUsers type={type}/>}/>}

                                {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                    <Route path="home" element={<ManageUsers/>}/>}

                                {type === "INSTRUCTOR" &&
                                    <Route path="home" element={<WorkPlan/>}/>}

                                {type === "GENERAL_SUPERVISOR" &&
                                    <Route path="home" element={<InfoViewer/>}/>}

                            {/*TODO: maybe put survey inside user?*/}
                            </Route>

                            {type === "SUPERVISOR" &&
                                <Route path="survey">
                                    <Route path="menu" element={<SurveyMenu />}/>
                                    <Route path="createSurvey" element={<SurveyBuilder/>}/>
                                    <Route path="getSurvey" element={<Survey/>}/>
                                    <Route path="surveyConstraint" element={<SurveyConstraintBuilder/>}/>
                                </Route>}

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

