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
import WorkReport from "./Pages/WorkReport/WorkReport";

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
import TaskIcon from '@mui/icons-material/Task';
import Connection from "./Communication/Connection";
import PasswordAuthentication from "./Pages/Credentials/PasswordAuthentication";
import GoalsManagement from "./Pages/GoalsManagement/GoalsManagement";
import MenuIcon from '@mui/icons-material/Menu';
import ProfilePage from "./Pages/Profile/ProfilePage";
import AccountBoxIcon from '@mui/icons-material/AccountBox';

// TODO: prevent users from going through the site by entering paths in the url
// TODO: currently saving everything in local storage but IT IS NOT SAFE
// TODO: change the usage of document.location.href with the useNavigate hook (example in SurveyMenu.js) in the whole project
// TODO: when the user closes the window log the user out before it closes

function App(){
    // general state data
    const [type, setType] = useState('GUEST') //TODO: change back to 'GUEST' when not developing
    const [openSidebar, setOpenSidebar] = useState(false);
    const [hideBars, setHideBars] = useState(false);

    // authentication related
    const [authAvailability, setAuthAvailability] = useState(false);
    const [authCallback, setAuthCallback] = useState(() => () => {console.log("not auth callback")});
    const [authCalleePage, setAuthCalleePage] = useState(''); // todo: is there's a better way to do it? i do it that way cause i override the history stack and can't just go back

    let navigate = useNavigate();

    //let type = "SUPERVISOR";
    const barWidth = "10%";
    const sidebarWidth = "15%";
    const page_does_not_exist_string = "דף זה אינו קיים";
    const logout_button_string = "יציאה";
    // TODO: the greetings currently doesn't work well. but perhaps once TAL implements what i asked then it will (return the username with the response for the request)
    const greetings_string = "שלום " + window.sessionStorage.getItem('username') // TODO: instead of the username, use the actual name of the user

    const styles = theme => ({
        appBar: {
            zIndex: theme.zIndex.drawer + 1,
        },

        toolbar: theme.mixins.toolbar,
    })

    /**
     * a callback to call when the result of the logout request got back
     * @param data the response for the logout request
     */
    const logoutCallback = (data) => {
        setType("GUEST")
        window.sessionStorage.setItem('username', data.result)
        setHideBars(true);
        navigate('/user/login', {replace: true})
    }

    /**
     * handler for when the logout button is pressed
     */
    const handleLogout = () => {
        console.log("sending logout")
        Connection.getInstance().logout(
           window.sessionStorage.getItem('username'),
            logoutCallback)
    }

    useEffect(() => {
        // TODO: needed?
    }, );

    // sidebar
    const drawer = (
        <Space.Fill>
            {/*TODO: show buttons based on permissions*/}
            <List>
                {/*home button*/}
                <ListItem button onClick={() => navigate(`user/home`, {replace: false})}>
                    <ListItemIcon>
                        <HomeIcon/>
                    </ListItemIcon>
                    <ListItemText primary="בית"/>
                </ListItem>
                {/*profile button*/}
                <ListItem button onClick={() => navigate(`user/profile`, {replace: false})}>
                    <ListItemIcon>
                        <AccountBoxIcon/>
                    </ListItemIcon>
                    <ListItemText primary="פרופיל"/>
                </ListItem>
                {/*survey button*/}
                <ListItem button onClick={() => navigate(`survey/menu`, {replace: false})}>
                    <ListItemIcon>
                        <PollIcon/>
                    </ListItemIcon>
                    <ListItemText primary="סקרים"/>
                </ListItem>
                {/*guiding baskets button*/}
                <ListItem button onClick={() => navigate(`user/guidingBasketsSearch`, {replace: false})}>
                    <ListItemIcon>
                        <ShoppingBasketIcon/>
                    </ListItemIcon>
                    <ListItemText primary="סלי הדרכה"/>
                </ListItem>
                {/*work report button*/}
                <ListItem button onClick={() => navigate(`user/workReport`, {replace: false})}>
                    <ListItemIcon>
                        <SummarizeIcon/>
                    </ListItemIcon>
                    <ListItemText primary='דו"ח עבודה'/>
                </ListItem>
                {/*goals management button*/}
                <ListItem button onClick={() => navigate(`user/goalsManagement`, {replace: false})}>
                    <ListItemIcon>
                        <TaskIcon/>
                    </ListItemIcon>
                    <ListItemText primary='ניהול יעדים'/>
                </ListItem>
            </List>
        </Space.Fill>
    );

    return (
        <div dir="rtl">
            <Space.ViewPort>
                {/* app bar */}
                {!hideBars && <Space.Top size={barWidth}>
                    {/* TODO: fix it so the card line would be see and it would align with the logo*/}
                    <AppBar title='מענ"ה' color="background" position="static">
                        <Toolbar>
                            {/*menu button*/}
                            <IconButton
                                color="inherit"
                                aria-label="Menu"
                                onClick={() => setOpenSidebar(!openSidebar)}
                            >
                                <MenuIcon />
                            </IconButton>
                            {/*logo*/}
                            <Typography
                                variant="h6"
                                noWrap
                                component="div"
                            >
                                מענ"ה
                            </Typography>
                            {/*profile button*/}
                            <IconButton
                                size="large"
                                aria-label="account of current user"
                                aria-controls="menu-appbar"
                                aria-haspopup="true"
                                color="inherit"
                                onClick={() => navigate(`user/profile`, {replace: false})}
                            >
                                <AccountCircle />
                            </IconButton>
                            {/*greetings text*/}
                            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                                {greetings_string}
                            </Typography>
                            {/*logout button*/}
                            <Button onClick={() => handleLogout()} color="inherit">{logout_button_string}</Button>
                        </Toolbar>
                    </AppBar>
                    {/*sidebar*/}
                    {/*todo: make the drawer below the appbar*/}
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
                        open={openSidebar}
                    >
                        {drawer}
                    </Drawer>
                </Space.Top>}
                <Space.Fill>
                    {/* routes to the different screens */}
                    <Routes>
                        {/*TODO: find a more elegant way for the permissions*/}
                        <Route path="user">
                            <Route path="login" element={<Login changeType={setType} setHideBars={setHideBars}/>}/>
                            {authAvailability && <Route path="auth" element={<PasswordAuthentication callback={authCallback} callee={authCalleePage} setHideBars={setHideBars}/>}/>}
                            <Route path="profile" element={<ProfilePage/>}/>

                            {(type === "SUPERVISOR" || type === "INSTRUCTOR") &&
                                <Route path="guidingBasketsSearch" element={<GuidingBaskets/>}/>}

                            {(type === "SUPERVISOR" || type === "INSTRUCTOR") &&
                                <Route path="workReport" element={<WorkReport/>}/>}

                            {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                <Route path="registerUsers" element={<RegisterUsers type={type}/>}/>}

                            {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                <Route path="goalsManagement" element={<GoalsManagement/>}/>}}}

                            {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                <Route path="home" element={<ManageUsers userType={type} setAuthAvailability={setAuthAvailability} setAuthCallBack={setAuthCallback} setAuthCalleePage={setAuthCalleePage} setHideBars={setHideBars}/>}/>}

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
//         {!document.location.href.includes("login") &&
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
//                             {/*
//                         </Typography>
//                         {/*
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
//                     {/*
//                     <Route path="survey/survey" element={<Survey/>}/>
//                     <Route
//                         path="*"
//                         element={
//                             <main style={{ padding: "1rem" }}> {/*
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

